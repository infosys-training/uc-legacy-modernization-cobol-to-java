package com.carddemo.batch.job;

import com.carddemo.model.Account;
import com.carddemo.model.Card;
import com.carddemo.model.CardXref;
import com.carddemo.model.Customer;
import com.carddemo.model.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Spring Batch job equivalent of CBEXPORT.cbl — Export data to sequential files.
 *
 * Exports all VSAM data to delimited files for branch migration or backup.
 * Original COBOL reads each VSAM file sequentially and writes to flat files.
 *
 * Export format: Pipe-delimited (|) with header row.
 * Output files: accounts.dat, customers.dat, cards.dat, cardxref.dat, transactions.dat
 */
@Configuration
public class DataExportJobConfig {

    private static final Logger log = LoggerFactory.getLogger(DataExportJobConfig.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${carddemo.batch.export.output-dir:./export}")
    private String outputDir;

    @Bean
    public Job dataExportJob(JobRepository jobRepository,
                             Step exportAccountsStep,
                             Step exportCustomersStep,
                             Step exportCardsStep,
                             Step exportTransactionsStep) {
        return new JobBuilder("dataExportJob", jobRepository)
                .start(exportAccountsStep)
                .next(exportCustomersStep)
                .next(exportCardsStep)
                .next(exportTransactionsStep)
                .build();
    }

    @Bean
    public Step exportAccountsStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("exportAccountsStep", jobRepository)
                .tasklet(exportAccountsTasklet(), txManager)
                .build();
    }

    @Bean
    public Step exportCustomersStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("exportCustomersStep", jobRepository)
                .tasklet(exportCustomersTasklet(), txManager)
                .build();
    }

    @Bean
    public Step exportCardsStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("exportCardsStep", jobRepository)
                .tasklet(exportCardsTasklet(), txManager)
                .build();
    }

    @Bean
    public Step exportTransactionsStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("exportTransactionsStep", jobRepository)
                .tasklet(exportTransactionsTasklet(), txManager)
                .build();
    }

    @Bean
    public Tasklet exportAccountsTasklet() {
        return (contribution, chunkContext) -> {
            Path outPath = createOutputDir();
            List<Account> accounts = entityManager.createQuery("SELECT a FROM Account a ORDER BY a.acctId", Account.class)
                    .getResultList();

            try (BufferedWriter writer = Files.newBufferedWriter(outPath.resolve("accounts.dat"))) {
                writer.write("ACCT_ID|STATUS|CURR_BAL|CREDIT_LIMIT|CASH_LIMIT|OPEN_DATE|EXP_DATE|CYC_CREDIT|CYC_DEBIT|GROUP_ID");
                writer.newLine();
                for (Account a : accounts) {
                    writer.write(String.format("%d|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                            a.getAcctId(), a.getActiveStatus(), a.getCurrentBalance(),
                            a.getCreditLimit(), a.getCashCreditLimit(),
                            a.getOpenDate() != null ? a.getOpenDate() : "",
                            a.getExpirationDate() != null ? a.getExpirationDate() : "",
                            a.getCurrentCycleCredit(), a.getCurrentCycleDebit(),
                            a.getGroupId() != null ? a.getGroupId() : ""));
                    writer.newLine();
                }
            }
            log.info("Exported {} accounts", accounts.size());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet exportCustomersTasklet() {
        return (contribution, chunkContext) -> {
            Path outPath = createOutputDir();
            List<Customer> customers = entityManager.createQuery("SELECT c FROM Customer c ORDER BY c.custId", Customer.class)
                    .getResultList();

            try (BufferedWriter writer = Files.newBufferedWriter(outPath.resolve("customers.dat"))) {
                writer.write("CUST_ID|FIRST_NAME|MIDDLE_NAME|LAST_NAME|SSN|DOB|FICO_SCORE|ADDR_LINE1|ADDR_LINE2|ADDR_STATE|ADDR_ZIP|PHONE1|PHONE2");
                writer.newLine();
                for (Customer c : customers) {
                    writer.write(String.format("%d|%s|%s|%s|%s|%s|%d|%s|%s|%s|%s|%s|%s",
                            c.getCustId(),
                            nullSafe(c.getFirstName()), nullSafe(c.getMiddleName()), nullSafe(c.getLastName()),
                            nullSafe(c.getSsn()), c.getDateOfBirth() != null ? c.getDateOfBirth() : "",
                            c.getFicoCreditScore() != null ? c.getFicoCreditScore() : 0,
                            nullSafe(c.getAddressLine1()), nullSafe(c.getAddressLine2()),
                            nullSafe(c.getAddressStateCode()), nullSafe(c.getAddressZip()),
                            nullSafe(c.getPhoneNumber1()), nullSafe(c.getPhoneNumber2())));
                    writer.newLine();
                }
            }
            log.info("Exported {} customers", customers.size());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet exportCardsTasklet() {
        return (contribution, chunkContext) -> {
            Path outPath = createOutputDir();
            List<CardXref> xrefs = entityManager.createQuery("SELECT x FROM CardXref x ORDER BY x.cardNumber", CardXref.class)
                    .getResultList();

            try (BufferedWriter writer = Files.newBufferedWriter(outPath.resolve("cardxref.dat"))) {
                writer.write("CARD_NUM|ACCT_ID|CUST_ID");
                writer.newLine();
                for (CardXref x : xrefs) {
                    writer.write(String.format("%s|%d|%d",
                            x.getCardNumber(), x.getAccountId(), x.getCustomerId()));
                    writer.newLine();
                }
            }
            log.info("Exported {} card cross-references", xrefs.size());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet exportTransactionsTasklet() {
        return (contribution, chunkContext) -> {
            Path outPath = createOutputDir();
            List<Transaction> transactions = entityManager.createQuery(
                            "SELECT t FROM Transaction t ORDER BY t.transactionId", Transaction.class)
                    .getResultList();

            try (BufferedWriter writer = Files.newBufferedWriter(outPath.resolve("transactions.dat"))) {
                writer.write("TRAN_ID|CARD_NUM|TYPE_CD|CAT_CD|AMOUNT|DESCRIPTION|MERCHANT_NAME|ORIG_TS|PROC_TS");
                writer.newLine();
                for (Transaction t : transactions) {
                    writer.write(String.format("%s|%s|%s|%d|%s|%s|%s|%s|%s",
                            t.getTransactionId(), t.getCardNumber(),
                            t.getTypeCode(),
                            t.getCategoryCode() != null ? t.getCategoryCode() : 0,
                            t.getAmount(),
                            nullSafe(t.getDescription()), nullSafe(t.getMerchantName()),
                            t.getOriginTimestamp() != null ? t.getOriginTimestamp() : "",
                            t.getProcessedTimestamp() != null ? t.getProcessedTimestamp() : ""));
                    writer.newLine();
                }
            }
            log.info("Exported {} transactions", transactions.size());
            return RepeatStatus.FINISHED;
        };
    }

    private Path createOutputDir() throws IOException {
        String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path outPath = Path.of(outputDir, dateSuffix);
        Files.createDirectories(outPath);
        return outPath;
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }
}
