package com.carddemo.batch.job;

import com.carddemo.batch.model.DailyTransaction;
import com.carddemo.batch.model.TransactionReject;
import com.carddemo.model.Account;
import com.carddemo.model.CardXref;
import com.carddemo.model.Transaction;
import com.carddemo.model.TransactionCategoryBalance;
import com.carddemo.model.TransactionCategoryBalance.TranCatBalanceId;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Batch job equivalent of CBTRN02C.cbl — Post daily transactions.
 *
 * Pipeline:
 *   1. Read unprocessed daily transactions (DALYTRAN-FILE)
 *   2. Validate: card exists in XREF, account exists, credit limit not exceeded, not expired
 *   3. Post valid transactions to TRANSACT file, update ACCOUNT balances, update TCATBAL
 *   4. Write rejected transactions to DALYREJS
 *
 * Original COBOL validation codes:
 *   100 = Invalid card number (XREF lookup failed)
 *   101 = Account record not found
 *   102 = Over-limit transaction
 *   103 = Transaction received after account expiration
 */
@Configuration
public class PostTransactionJobConfig {

    private static final Logger log = LoggerFactory.getLogger(PostTransactionJobConfig.class);

    private final AccountRepository accountRepository;
    private final CardXrefRepository cardXrefRepository;
    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PostTransactionJobConfig(AccountRepository accountRepository,
                                    CardXrefRepository cardXrefRepository,
                                    TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job postTransactionJob(JobRepository jobRepository, Step postTransactionStep) {
        return new JobBuilder("postTransactionJob", jobRepository)
                .start(postTransactionStep)
                .build();
    }

    @Bean
    public Step postTransactionStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("postTransactionStep", jobRepository)
                .<DailyTransaction, Object>chunk(100, transactionManager)
                .reader(dailyTransactionReader())
                .processor(dailyTransactionProcessor())
                .writer(dailyTransactionWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<DailyTransaction> dailyTransactionReader() {
        return new JpaPagingItemReaderBuilder<DailyTransaction>()
                .name("dailyTransactionReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("SELECT d FROM DailyTransaction d WHERE d.processed = false ORDER BY d.transactionId")
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<DailyTransaction, Object> dailyTransactionProcessor() {
        return dailyTran -> {
            // 1500-VALIDATE-TRAN equivalent
            // 1500-A-LOOKUP-XREF: Validate card number exists
            Optional<CardXref> xrefOpt = cardXrefRepository.findById(dailyTran.getCardNumber());
            if (xrefOpt.isEmpty()) {
                return createReject(dailyTran, 100, "INVALID CARD NUMBER FOUND");
            }

            CardXref xref = xrefOpt.get();

            // 1500-B-LOOKUP-ACCT: Validate account exists
            Optional<Account> acctOpt = accountRepository.findById(xref.getAccountId());
            if (acctOpt.isEmpty()) {
                return createReject(dailyTran, 101, "ACCOUNT RECORD NOT FOUND");
            }

            Account account = acctOpt.get();

            // Credit limit check: ACCT-CREDIT-LIMIT >= current_cycle_credit - current_cycle_debit + tran_amt
            BigDecimal projectedBalance = account.getCurrentCycleCredit()
                    .subtract(account.getCurrentCycleDebit())
                    .add(dailyTran.getAmount());
            if (account.getCreditLimit().compareTo(projectedBalance) < 0) {
                return createReject(dailyTran, 102, "OVERLIMIT TRANSACTION");
            }

            // Expiration date check
            if (account.getExpirationDate() != null
                    && dailyTran.getOriginTimestamp() != null
                    && dailyTran.getOriginTimestamp().toLocalDate().isAfter(account.getExpirationDate())) {
                return createReject(dailyTran, 103, "TRANSACTION RECEIVED AFTER ACCT EXPIRATION");
            }

            // Validation passed — create posted transaction
            Transaction posted = new Transaction();
            posted.setTransactionId(dailyTran.getTransactionId());
            posted.setCardNumber(dailyTran.getCardNumber());
            posted.setTypeCode(dailyTran.getTypeCode());
            posted.setCategoryCode(dailyTran.getCategoryCode());
            posted.setSource(dailyTran.getSource());
            posted.setDescription(dailyTran.getDescription());
            posted.setAmount(dailyTran.getAmount());
            posted.setMerchantId(dailyTran.getMerchantId() != null ? parseMerchantId(dailyTran.getMerchantId()) : null);
            posted.setMerchantName(dailyTran.getMerchantName());
            posted.setMerchantCity(dailyTran.getMerchantCity());
            posted.setMerchantZip(dailyTran.getMerchantZip());
            posted.setOriginTimestamp(dailyTran.getOriginTimestamp());
            posted.setProcessedTimestamp(LocalDateTime.now());

            return new PostResult(posted, account, xref.getAccountId(), dailyTran);
        };
    }

    @Bean
    public ItemWriter<Object> dailyTransactionWriter() {
        return items -> {
            int posted = 0, rejected = 0;
            for (Object item : items) {
                if (item instanceof TransactionReject reject) {
                    entityManager.persist(reject);
                    // Mark daily transaction as processed
                    markProcessed(reject.getTransactionId());
                    rejected++;
                } else if (item instanceof PostResult result) {
                    // 2900-WRITE-TRANSACTION-FILE
                    transactionRepository.save(result.transaction());

                    // 2800-UPDATE-ACCOUNT-REC: Update account cycle debit
                    Account account = result.account();
                    account.setCurrentCycleDebit(
                            account.getCurrentCycleDebit().add(result.transaction().getAmount()));
                    accountRepository.save(account);

                    // 2700-UPDATE-TCATBAL: Update or create category balance
                    updateCategoryBalance(result.accountId(), result.transaction());

                    // Mark daily transaction as processed
                    markProcessed(result.dailyTran().getTransactionId());
                    posted++;
                }
            }
            log.info("Batch chunk complete: {} posted, {} rejected", posted, rejected);
        };
    }

    private TransactionReject createReject(DailyTransaction tran, int code, String desc) {
        TransactionReject reject = new TransactionReject();
        reject.setTransactionId(tran.getTransactionId());
        reject.setFailReasonCode(code);
        reject.setFailReasonDescription(desc);
        reject.setRejectedAt(LocalDateTime.now());
        return reject;
    }

    private void markProcessed(String transactionId) {
        entityManager.createQuery(
                        "UPDATE DailyTransaction d SET d.processed = true WHERE d.transactionId = :id")
                .setParameter("id", transactionId)
                .executeUpdate();
    }

    private void updateCategoryBalance(Long accountId, Transaction tran) {
        TranCatBalanceId balId = new TranCatBalanceId();
        balId.setAccountId(accountId);
        balId.setTypeCode(tran.getTypeCode());
        balId.setCategoryCode(tran.getCategoryCode() != null ? tran.getCategoryCode() : 0);

        TransactionCategoryBalance bal = entityManager.find(TransactionCategoryBalance.class, balId);
        if (bal == null) {
            bal = new TransactionCategoryBalance();
            bal.setId(balId);
            bal.setBalance(tran.getAmount());
            entityManager.persist(bal);
        } else {
            bal.setBalance(bal.getBalance().add(tran.getAmount()));
            entityManager.merge(bal);
        }
    }

    private Long parseMerchantId(String merchantId) {
        try {
            return Long.parseLong(merchantId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record PostResult(Transaction transaction, Account account, Long accountId, DailyTransaction dailyTran) {}
}
