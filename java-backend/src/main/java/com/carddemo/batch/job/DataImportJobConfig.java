package com.carddemo.batch.job;

import com.carddemo.model.Account;
import com.carddemo.model.CardXref;
import com.carddemo.model.Customer;
import com.carddemo.model.Transaction;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.CustomerRepository;
import com.carddemo.repository.TransactionRepository;
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

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Spring Batch job equivalent of CBIMPORT.cbl — Import sequential data into DB.
 *
 * Reads pipe-delimited export files and imports them into the database with validation.
 * Original COBOL validates record lengths and key uniqueness before writing to VSAM.
 *
 * Import order: accounts → customers → cardxref → transactions (FK dependencies)
 */
@Configuration
public class DataImportJobConfig {

    private static final Logger log = LoggerFactory.getLogger(DataImportJobConfig.class);

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardXrefRepository cardXrefRepository;
    private final TransactionRepository transactionRepository;

    @Value("${carddemo.batch.import.input-dir:./import}")
    private String inputDir;

    public DataImportJobConfig(AccountRepository accountRepository,
                               CustomerRepository customerRepository,
                               CardXrefRepository cardXrefRepository,
                               TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job dataImportJob(JobRepository jobRepository,
                             Step importAccountsStep,
                             Step importCustomersStep,
                             Step importCardXrefStep,
                             Step importTransactionsStep) {
        return new JobBuilder("dataImportJob", jobRepository)
                .start(importAccountsStep)
                .next(importCustomersStep)
                .next(importCardXrefStep)
                .next(importTransactionsStep)
                .build();
    }

    @Bean
    public Step importAccountsStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("importAccountsStep", jobRepository)
                .tasklet(importAccountsTasklet(), txManager)
                .build();
    }

    @Bean
    public Step importCustomersStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("importCustomersStep", jobRepository)
                .tasklet(importCustomersTasklet(), txManager)
                .build();
    }

    @Bean
    public Step importCardXrefStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("importCardXrefStep", jobRepository)
                .tasklet(importCardXrefTasklet(), txManager)
                .build();
    }

    @Bean
    public Step importTransactionsStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("importTransactionsStep", jobRepository)
                .tasklet(importTransactionsTasklet(), txManager)
                .build();
    }

    @Bean
    public Tasklet importAccountsTasklet() {
        return (contribution, chunkContext) -> {
            Path file = Path.of(inputDir, "accounts.dat");
            if (!Files.exists(file)) {
                log.warn("accounts.dat not found in {}, skipping", inputDir);
                return RepeatStatus.FINISHED;
            }
            int count = 0;
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String header = reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|", -1);
                    if (fields.length < 10) continue;

                    Account a = new Account();
                    a.setAcctId(Long.parseLong(fields[0].trim()));
                    a.setActiveStatus(fields[1].trim());
                    a.setCurrentBalance(new BigDecimal(fields[2].trim()));
                    a.setCreditLimit(new BigDecimal(fields[3].trim()));
                    a.setCashCreditLimit(new BigDecimal(fields[4].trim()));
                    a.setOpenDate(parseDate(fields[5]));
                    a.setExpirationDate(parseDate(fields[6]));
                    a.setCurrentCycleCredit(new BigDecimal(fields[7].trim()));
                    a.setCurrentCycleDebit(new BigDecimal(fields[8].trim()));
                    a.setGroupId(fields[9].trim().isEmpty() ? null : fields[9].trim());
                    accountRepository.save(a);
                    count++;
                }
            }
            log.info("Imported {} accounts", count);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet importCustomersTasklet() {
        return (contribution, chunkContext) -> {
            Path file = Path.of(inputDir, "customers.dat");
            if (!Files.exists(file)) {
                log.warn("customers.dat not found in {}, skipping", inputDir);
                return RepeatStatus.FINISHED;
            }
            int count = 0;
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|", -1);
                    if (fields.length < 13) continue;

                    Customer c = new Customer();
                    c.setCustId(Long.parseLong(fields[0].trim()));
                    c.setFirstName(fields[1].trim());
                    c.setMiddleName(fields[2].trim().isEmpty() ? null : fields[2].trim());
                    c.setLastName(fields[3].trim());
                    c.setSsn(fields[4].trim().isEmpty() ? null : fields[4].trim());
                    c.setDateOfBirth(parseDate(fields[5]));
                    c.setFicoCreditScore(fields[6].trim().isEmpty() ? null : Integer.parseInt(fields[6].trim()));
                    c.setAddressLine1(fields[7].trim().isEmpty() ? null : fields[7].trim());
                    c.setAddressLine2(fields[8].trim().isEmpty() ? null : fields[8].trim());
                    c.setAddressStateCode(fields[9].trim().isEmpty() ? null : fields[9].trim());
                    c.setAddressZip(fields[10].trim().isEmpty() ? null : fields[10].trim());
                    c.setPhoneNumber1(fields[11].trim().isEmpty() ? null : fields[11].trim());
                    c.setPhoneNumber2(fields[12].trim().isEmpty() ? null : fields[12].trim());
                    customerRepository.save(c);
                    count++;
                }
            }
            log.info("Imported {} customers", count);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet importCardXrefTasklet() {
        return (contribution, chunkContext) -> {
            Path file = Path.of(inputDir, "cardxref.dat");
            if (!Files.exists(file)) {
                log.warn("cardxref.dat not found in {}, skipping", inputDir);
                return RepeatStatus.FINISHED;
            }
            int count = 0;
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|", -1);
                    if (fields.length < 3) continue;

                    CardXref x = new CardXref();
                    x.setCardNumber(fields[0].trim());
                    x.setAccountId(Long.parseLong(fields[1].trim()));
                    x.setCustomerId(Long.parseLong(fields[2].trim()));
                    cardXrefRepository.save(x);
                    count++;
                }
            }
            log.info("Imported {} card cross-references", count);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet importTransactionsTasklet() {
        return (contribution, chunkContext) -> {
            Path file = Path.of(inputDir, "transactions.dat");
            if (!Files.exists(file)) {
                log.warn("transactions.dat not found in {}, skipping", inputDir);
                return RepeatStatus.FINISHED;
            }
            int count = 0;
            try (BufferedReader reader = Files.newBufferedReader(file)) {
                reader.readLine(); // skip header
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|", -1);
                    if (fields.length < 9) continue;

                    Transaction t = new Transaction();
                    t.setTransactionId(fields[0].trim());
                    t.setCardNumber(fields[1].trim());
                    t.setTypeCode(fields[2].trim());
                    t.setCategoryCode(fields[3].trim().isEmpty() ? 0 : Integer.parseInt(fields[3].trim()));
                    t.setAmount(new BigDecimal(fields[4].trim()));
                    t.setDescription(fields[5].trim().isEmpty() ? null : fields[5].trim());
                    t.setMerchantName(fields[6].trim().isEmpty() ? null : fields[6].trim());
                    t.setOriginTimestamp(parseDateTime(fields[7]));
                    t.setProcessedTimestamp(parseDateTime(fields[8]));
                    transactionRepository.save(t);
                    count++;
                }
            }
            log.info("Imported {} transactions", count);
            return RepeatStatus.FINISHED;
        };
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
