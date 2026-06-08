package com.carddemo.batch.job;

import com.carddemo.model.Account;
import com.carddemo.model.CardXref;
import com.carddemo.model.Customer;
import com.carddemo.model.Transaction;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.CustomerRepository;
import com.carddemo.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Spring Batch job equivalent of CBSTM03A.CBL — Statement generation.
 *
 * Pipeline:
 *   1. Read all active accounts
 *   2. For each account, look up customer + cards (via XREF) + recent transactions
 *   3. Generate statement in both TEXT and HTML format
 *   4. Write to output directory
 *
 * Original COBOL generates two output files:
 *   - STMT-FILE (sequential text, 132-col report format)
 *   - HTML-FILE (basic HTML table format)
 *
 * This implementation generates individual statement files per account (text + HTML).
 */
@Configuration
public class StatementGenerationJobConfig {

    private static final Logger log = LoggerFactory.getLogger(StatementGenerationJobConfig.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardXrefRepository cardXrefRepository;
    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${carddemo.batch.statements.output-dir:./statements}")
    private String outputDir;

    public StatementGenerationJobConfig(AccountRepository accountRepository,
                                        CustomerRepository customerRepository,
                                        CardXrefRepository cardXrefRepository,
                                        TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job statementGenerationJob(JobRepository jobRepository, Step statementGenerationStep) {
        return new JobBuilder("statementGenerationJob", jobRepository)
                .start(statementGenerationStep)
                .build();
    }

    @Bean
    public Step statementGenerationStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("statementGenerationStep", jobRepository)
                .<Account, StatementData>chunk(20, transactionManager)
                .reader(statementAccountReader())
                .processor(statementProcessor())
                .writer(statementWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Account> statementAccountReader() {
        return new JpaPagingItemReaderBuilder<Account>()
                .name("statementAccountReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("SELECT a FROM Account a WHERE a.activeStatus = 'Y' ORDER BY a.acctId")
                .pageSize(20)
                .build();
    }

    @Bean
    public ItemProcessor<Account, StatementData> statementProcessor() {
        return account -> {
            // Look up cards linked to this account
            List<CardXref> cards = cardXrefRepository.findByAccountId(account.getAcctId());
            if (cards.isEmpty()) {
                return null;
            }

            // Look up customer from first card's XREF
            Customer customer = null;
            for (CardXref xref : cards) {
                var custOpt = customerRepository.findById(xref.getCustomerId());
                if (custOpt.isPresent()) {
                    customer = custOpt.get();
                    break;
                }
            }
            if (customer == null) {
                return null;
            }

            // Get recent transactions for this account's cards
            List<String> cardNumbers = cards.stream()
                    .map(CardXref::getCardNumber)
                    .toList();

            List<Transaction> transactions = entityManager.createQuery(
                            "SELECT t FROM Transaction t WHERE t.cardNumber IN :cards ORDER BY t.originTimestamp DESC",
                            Transaction.class)
                    .setParameter("cards", cardNumbers)
                    .setMaxResults(100)
                    .getResultList();

            return new StatementData(account, customer, cards, transactions);
        };
    }

    @Bean
    public ItemWriter<StatementData> statementWriter() {
        return statements -> {
            Path outPath = Path.of(outputDir);
            Files.createDirectories(outPath);
            String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            int count = 0;
            for (StatementData stmt : statements) {
                String fileBase = String.format("stmt_%011d_%s", stmt.account().getAcctId(), dateSuffix);

                // Write text statement
                String textContent = generateTextStatement(stmt);
                Files.writeString(outPath.resolve(fileBase + ".txt"), textContent,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Write HTML statement
                String htmlContent = generateHtmlStatement(stmt);
                Files.writeString(outPath.resolve(fileBase + ".html"), htmlContent,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                count++;
            }
            log.info("Generated {} statement pairs (text + HTML)", count);
        };
    }

    private String generateTextStatement(StatementData stmt) {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(80);

        sb.append(line).append("\n");
        sb.append(String.format("  CARDDEMO - MONTHLY STATEMENT          Date: %s%n", DATE_FMT.format(LocalDate.now())));
        sb.append(line).append("\n\n");
        sb.append(String.format("  Account:    %011d%n", stmt.account().getAcctId()));
        sb.append(String.format("  Customer:   %s %s%n", stmt.customer().getFirstName(), stmt.customer().getLastName()));
        sb.append(String.format("  Status:     %s%n", "Y".equals(stmt.account().getActiveStatus()) ? "Active" : "Inactive"));
        sb.append(String.format("  Balance:    $%,.2f%n", stmt.account().getCurrentBalance()));
        sb.append(String.format("  Credit Limit: $%,.2f%n", stmt.account().getCreditLimit()));
        sb.append(String.format("  Available:  $%,.2f%n",
                stmt.account().getCreditLimit().subtract(stmt.account().getCurrentBalance())));
        sb.append("\n");
        sb.append(String.format("  %-16s %-12s %-6s %12s  %-30s%n", "TRAN ID", "DATE", "TYPE", "AMOUNT", "DESCRIPTION"));
        sb.append("  ").append("-".repeat(78)).append("\n");

        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : stmt.transactions()) {
            String date = t.getOriginTimestamp() != null
                    ? DATE_FMT.format(t.getOriginTimestamp()) : "N/A";
            sb.append(String.format("  %-16s %-12s %-6s %12.2f  %-30s%n",
                    t.getTransactionId(), date, t.getTypeCode(),
                    t.getAmount(), truncate(t.getDescription(), 30)));
            total = total.add(t.getAmount());
        }

        sb.append("  ").append("-".repeat(78)).append("\n");
        sb.append(String.format("  %-36s %12.2f%n", "TOTAL:", total));
        sb.append("\n").append(line).append("\n");
        sb.append("  End of Statement\n");

        return sb.toString();
    }

    private String generateHtmlStatement(StatementData stmt) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\">\n");
        sb.append("<title>Statement - Account ").append(String.format("%011d", stmt.account().getAcctId())).append("</title>\n");
        sb.append("<style>body{font-family:Arial,sans-serif;margin:20px}table{border-collapse:collapse;width:100%}");
        sb.append("th,td{border:1px solid #ddd;padding:8px;text-align:left}th{background:#4472C4;color:white}");
        sb.append("tr:nth-child(even){background:#f2f2f2}.header{background:#2F5496;color:white;padding:20px}");
        sb.append(".summary{margin:20px 0}.amount{text-align:right}</style>\n</head><body>\n");

        sb.append("<div class=\"header\"><h1>CardDemo Monthly Statement</h1>");
        sb.append("<p>Generated: ").append(DATE_FMT.format(LocalDate.now())).append("</p></div>\n");

        sb.append("<div class=\"summary\">\n");
        sb.append("<p><strong>Account:</strong> ").append(String.format("%011d", stmt.account().getAcctId())).append("</p>\n");
        sb.append("<p><strong>Customer:</strong> ").append(stmt.customer().getFirstName())
                .append(" ").append(stmt.customer().getLastName()).append("</p>\n");
        sb.append("<p><strong>Balance:</strong> $").append(String.format("%,.2f", stmt.account().getCurrentBalance())).append("</p>\n");
        sb.append("<p><strong>Credit Limit:</strong> $").append(String.format("%,.2f", stmt.account().getCreditLimit())).append("</p>\n");
        sb.append("<p><strong>Available Credit:</strong> $").append(String.format("%,.2f",
                stmt.account().getCreditLimit().subtract(stmt.account().getCurrentBalance()))).append("</p>\n");
        sb.append("</div>\n");

        sb.append("<table>\n<thead><tr><th>Transaction ID</th><th>Date</th><th>Type</th>");
        sb.append("<th>Amount</th><th>Description</th><th>Merchant</th></tr></thead>\n<tbody>\n");

        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : stmt.transactions()) {
            String date = t.getOriginTimestamp() != null ? DATE_FMT.format(t.getOriginTimestamp()) : "N/A";
            sb.append("<tr><td>").append(t.getTransactionId()).append("</td>");
            sb.append("<td>").append(date).append("</td>");
            sb.append("<td>").append(t.getTypeCode()).append("</td>");
            sb.append("<td class=\"amount\">$").append(String.format("%,.2f", t.getAmount())).append("</td>");
            sb.append("<td>").append(t.getDescription() != null ? t.getDescription() : "").append("</td>");
            sb.append("<td>").append(t.getMerchantName() != null ? t.getMerchantName() : "").append("</td></tr>\n");
            total = total.add(t.getAmount());
        }

        sb.append("</tbody>\n<tfoot><tr><td colspan=\"3\"><strong>Total</strong></td>");
        sb.append("<td class=\"amount\"><strong>$").append(String.format("%,.2f", total)).append("</strong></td>");
        sb.append("<td colspan=\"2\"></td></tr></tfoot>\n</table>\n");
        sb.append("<p style=\"margin-top:20px;color:#666;font-size:12px\">This is an auto-generated statement. ");
        sb.append("Please contact support for any discrepancies.</p>\n");
        sb.append("</body></html>");

        return sb.toString();
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }

    public record StatementData(Account account, Customer customer, List<CardXref> cards, List<Transaction> transactions) {}
}
