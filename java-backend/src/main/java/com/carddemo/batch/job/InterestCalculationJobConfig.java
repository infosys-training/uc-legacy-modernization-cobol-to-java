package com.carddemo.batch.job;

import com.carddemo.model.Account;
import com.carddemo.model.DisclosureGroup;
import com.carddemo.model.DisclosureGroup.DisclosureGroupId;
import com.carddemo.model.Transaction;
import com.carddemo.model.TransactionCategoryBalance;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.DisclosureGroupRepository;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Spring Batch job equivalent of CBACT04C.cbl — Interest calculation.
 *
 * Pipeline:
 *   1. Read all transaction category balances (TCATBAL-FILE, sequential by account)
 *   2. For each balance record, look up interest rate from disclosure group
 *   3. Compute monthly interest = balance * (annual_rate / 12 / 100)
 *   4. Accumulate interest per account
 *   5. When account changes, update account current balance with total interest
 *   6. Write interest transaction record for audit trail
 *
 * Matches COBOL logic:
 *   - 1200-GET-INTEREST-RATE: lookup DISCGRP by (ACCT-GROUP-ID, TRANCAT-TYPE-CD, TRANCAT-CD)
 *   - 1300-COMPUTE-INTEREST: monthly_int = balance * (rate / 1200)
 *   - 1050-UPDATE-ACCOUNT: ADD total_int TO ACCT-CURR-BAL; MOVE 0 TO CYC-CREDIT/DEBIT
 */
@Configuration
public class InterestCalculationJobConfig {

    private static final Logger log = LoggerFactory.getLogger(InterestCalculationJobConfig.class);
    private static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("1200");

    private final AccountRepository accountRepository;
    private final DisclosureGroupRepository disclosureGroupRepository;
    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public InterestCalculationJobConfig(AccountRepository accountRepository,
                                        DisclosureGroupRepository disclosureGroupRepository,
                                        TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.disclosureGroupRepository = disclosureGroupRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job interestCalculationJob(JobRepository jobRepository, Step interestCalculationStep) {
        return new JobBuilder("interestCalculationJob", jobRepository)
                .start(interestCalculationStep)
                .build();
    }

    @Bean
    public Step interestCalculationStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("interestCalculationStep", jobRepository)
                .<Account, InterestResult>chunk(50, transactionManager)
                .reader(activeAccountReader())
                .processor(interestProcessor())
                .writer(interestWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Account> activeAccountReader() {
        return new JpaPagingItemReaderBuilder<Account>()
                .name("activeAccountReader")
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .queryString("SELECT a FROM Account a WHERE a.activeStatus = 'Y' ORDER BY a.acctId")
                .pageSize(50)
                .build();
    }

    @Bean
    public ItemProcessor<Account, InterestResult> interestProcessor() {
        return account -> {
            if (account.getGroupId() == null || account.getGroupId().isBlank()) {
                return null; // skip accounts without group assignment
            }

            // Query all category balances for this account
            var balances = entityManager.createQuery(
                            "SELECT tcb FROM TransactionCategoryBalance tcb WHERE tcb.id.accountId = :acctId",
                            TransactionCategoryBalance.class)
                    .setParameter("acctId", account.getAcctId())
                    .getResultList();

            BigDecimal totalInterest = BigDecimal.ZERO;

            for (TransactionCategoryBalance catBal : balances) {
                // 1200-GET-INTEREST-RATE: look up disclosure group
                DisclosureGroupId dgId = new DisclosureGroupId();
                dgId.setAccountGroupId(account.getGroupId().trim());
                dgId.setTransactionTypeCode(catBal.getId().getTypeCode());
                dgId.setTransactionCategoryCode(catBal.getId().getCategoryCode());

                DisclosureGroup dg = entityManager.find(DisclosureGroup.class, dgId);
                if (dg == null || dg.getInterestRate().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                // 1300-COMPUTE-INTEREST: monthly_interest = balance * (rate / 1200)
                BigDecimal monthlyRate = dg.getInterestRate().divide(MONTHS_PER_YEAR, 10, RoundingMode.HALF_UP);
                BigDecimal interest = catBal.getBalance().multiply(monthlyRate)
                        .setScale(2, RoundingMode.HALF_UP);

                totalInterest = totalInterest.add(interest);
            }

            if (totalInterest.compareTo(BigDecimal.ZERO) == 0) {
                return null;
            }

            return new InterestResult(account, totalInterest);
        };
    }

    @Bean
    public ItemWriter<InterestResult> interestWriter() {
        return results -> {
            int processed = 0;
            for (InterestResult result : results) {
                Account account = result.account();

                // 1050-UPDATE-ACCOUNT: ADD total_int TO ACCT-CURR-BAL, reset cycle counters
                account.setCurrentBalance(account.getCurrentBalance().add(result.totalInterest()));
                account.setCurrentCycleCredit(BigDecimal.ZERO);
                account.setCurrentCycleDebit(BigDecimal.ZERO);
                accountRepository.save(account);

                // Write interest charge transaction for audit trail
                Transaction interestTran = new Transaction();
                interestTran.setTransactionId("INT" + UUID.randomUUID().toString().substring(0, 13));
                interestTran.setCardNumber("SYSTEM");
                interestTran.setTypeCode("IN");
                interestTran.setCategoryCode(0);
                interestTran.setSource("BATCH");
                interestTran.setDescription("Monthly interest charge");
                interestTran.setAmount(result.totalInterest());
                interestTran.setOriginTimestamp(LocalDateTime.now());
                interestTran.setProcessedTimestamp(LocalDateTime.now());
                transactionRepository.save(interestTran);

                processed++;
            }
            log.info("Interest calculated for {} accounts", processed);
        };
    }

    public record InterestResult(Account account, BigDecimal totalInterest) {}
}
