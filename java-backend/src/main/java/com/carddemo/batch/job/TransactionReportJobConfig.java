package com.carddemo.batch.job;

import com.carddemo.model.CardXref;
import com.carddemo.model.Transaction;
import com.carddemo.model.TransactionType;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.TransactionTypeRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Spring Batch job equivalent of CBTRN03C.cbl — Transaction detail report.
 *
 * Pipeline:
 *   1. Read all transactions (TRANSACT-FILE, sequential)
 *   2. For each, look up card XREF and transaction type description
 *   3. Format into a columnar report
 *   4. Write report to output file (REPTFILE equivalent)
 *
 * Original COBOL reads DATE-PARMS-FILE for reporting date range.
 * This implementation uses configurable date parameters.
 */
@Configuration
public class TransactionReportJobConfig {

    private static final Logger log = LoggerFactory.getLogger(TransactionReportJobConfig.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CardXrefRepository cardXrefRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${carddemo.batch.reports.output-dir:./reports}")
    private String outputDir;

    public TransactionReportJobConfig(CardXrefRepository cardXrefRepository,
                                      TransactionTypeRepository transactionTypeRepository) {
        this.cardXrefRepository = cardXrefRepository;
        this.transactionTypeRepository = transactionTypeRepository;
    }

    @Bean
    public Job transactionReportJob(JobRepository jobRepository, Step transactionReportStep) {
        return new JobBuilder("transactionReportJob", jobRepository)
                .start(transactionReportStep)
                .build();
    }

    @Bean
    public Step transactionReportStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager) {
        return new StepBuilder("transactionReportStep", jobRepository)
                .<Transaction, ReportLine>chunk(200, transactionManager)
                .reader(reportTransactionReader())
                .processor(reportLineProcessor())
                .writer(reportFileWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Transaction> reportTransactionReader() {
        return new JpaPagingItemReaderBuilder<Transaction>()
                .name("reportTransactionReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("SELECT t FROM Transaction t ORDER BY t.originTimestamp DESC, t.transactionId")
                .pageSize(200)
                .build();
    }

    @Bean
    public ItemProcessor<Transaction, ReportLine> reportLineProcessor() {
        return transaction -> {
            // Look up card XREF for account info
            String accountId = "N/A";
            Optional<CardXref> xref = cardXrefRepository.findById(transaction.getCardNumber());
            if (xref.isPresent()) {
                accountId = String.format("%011d", xref.get().getAccountId());
            }

            // Look up transaction type description
            String typeDesc = transaction.getTypeCode();
            Optional<TransactionType> type = transactionTypeRepository.findById(transaction.getTypeCode());
            if (type.isPresent()) {
                typeDesc = type.get().getDescription();
            }

            return new ReportLine(
                    transaction.getTransactionId(),
                    accountId,
                    transaction.getCardNumber(),
                    transaction.getTypeCode(),
                    typeDesc,
                    transaction.getAmount(),
                    transaction.getOriginTimestamp() != null
                            ? TS_FMT.format(transaction.getOriginTimestamp()) : "N/A",
                    transaction.getDescription()
            );
        };
    }

    @Bean
    public ItemWriter<ReportLine> reportFileWriter() {
        return new ItemWriter<>() {
            private final List<ReportLine> allLines = new ArrayList<>();
            private boolean headerWritten = false;

            @Override
            public void write(org.springframework.batch.item.Chunk<? extends ReportLine> chunk) throws Exception {
                allLines.addAll(chunk.getItems());

                // Write report on each chunk (append mode)
                Path outPath = Path.of(outputDir);
                Files.createDirectories(outPath);

                String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Path reportFile = outPath.resolve("tranrept_" + dateSuffix + ".txt");

                StringBuilder sb = new StringBuilder();
                if (!headerWritten) {
                    sb.append(generateReportHeader());
                    headerWritten = true;
                }

                for (ReportLine line : chunk.getItems()) {
                    sb.append(formatReportLine(line));
                }

                Files.writeString(reportFile, sb.toString(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                log.info("Transaction report: wrote {} lines (total: {})", chunk.size(), allLines.size());
            }
        };
    }

    private String generateReportHeader() {
        StringBuilder sb = new StringBuilder();
        String sep = "=".repeat(132);
        sb.append(sep).append("\n");
        sb.append(String.format("  CARDDEMO - DAILY TRANSACTION DETAIL REPORT%70sDate: %s%n",
                "", DATE_FMT.format(LocalDate.now())));
        sb.append(sep).append("\n\n");
        sb.append(String.format("  %-16s %-11s %-16s %-4s %-20s %12s  %-19s  %-30s%n",
                "TRAN-ID", "ACCOUNT", "CARD-NUMBER", "TYPE", "TYPE-DESC", "AMOUNT", "TIMESTAMP", "DESCRIPTION"));
        sb.append("  ").append("-".repeat(130)).append("\n");
        return sb.toString();
    }

    private String formatReportLine(ReportLine line) {
        return String.format("  %-16s %-11s %-16s %-4s %-20s %12.2f  %-19s  %-30s%n",
                line.transactionId(), line.accountId(), line.cardNumber(),
                line.typeCode(), truncate(line.typeDescription(), 20),
                line.amount(), line.timestamp(),
                truncate(line.description(), 30));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    public record ReportLine(String transactionId, String accountId, String cardNumber,
                             String typeCode, String typeDescription, BigDecimal amount,
                             String timestamp, String description) {}
}
