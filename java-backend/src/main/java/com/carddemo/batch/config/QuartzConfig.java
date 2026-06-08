package com.carddemo.batch.config;

import com.carddemo.batch.quartz.InterestCalculationQuartzJob;
import com.carddemo.batch.quartz.PostTransactionQuartzJob;
import com.carddemo.batch.quartz.StatementGenerationQuartzJob;
import com.carddemo.batch.quartz.TransactionReportQuartzJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz Scheduler configuration mapping the COBOL daily batch pipeline:
 *   POSTTRAN (22:00) → INTCALC (02:00 1st) → CREASTMT (03:00 1st) → TRANREPT (06:00)
 *
 * Original scheduling was managed by Control-M with SMART_FOLDER containers.
 */
@Configuration
public class QuartzConfig {

    // --- PostTransaction Job (CBTRN02C equivalent) ---
    @Bean
    public JobDetail postTransactionJobDetail() {
        return JobBuilder.newJob(PostTransactionQuartzJob.class)
                .withIdentity("postTransactionJob", "DAILY_PIPELINE")
                .withDescription("Post daily transactions to master file (CBTRN02C)")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger postTransactionTrigger(JobDetail postTransactionJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(postTransactionJobDetail)
                .withIdentity("postTransactionTrigger", "DAILY_PIPELINE")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 22 * * ?"))
                .build();
    }

    // --- Interest Calculation Job (CBACT04C equivalent) ---
    @Bean
    public JobDetail interestCalculationJobDetail() {
        return JobBuilder.newJob(InterestCalculationQuartzJob.class)
                .withIdentity("interestCalculationJob", "MONTHLY_PIPELINE")
                .withDescription("Calculate interest on account balances (CBACT04C)")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger interestCalculationTrigger(JobDetail interestCalculationJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(interestCalculationJobDetail)
                .withIdentity("interestCalculationTrigger", "MONTHLY_PIPELINE")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1 * ?"))
                .build();
    }

    // --- Statement Generation Job (CBSTM03A equivalent) ---
    @Bean
    public JobDetail statementGenerationJobDetail() {
        return JobBuilder.newJob(StatementGenerationQuartzJob.class)
                .withIdentity("statementGenerationJob", "MONTHLY_PIPELINE")
                .withDescription("Generate customer account statements (CBSTM03A)")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger statementGenerationTrigger(JobDetail statementGenerationJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(statementGenerationJobDetail)
                .withIdentity("statementGenerationTrigger", "MONTHLY_PIPELINE")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 1 * ?"))
                .build();
    }

    // --- Transaction Report Job (CBTRN03C equivalent) ---
    @Bean
    public JobDetail transactionReportJobDetail() {
        return JobBuilder.newJob(TransactionReportQuartzJob.class)
                .withIdentity("transactionReportJob", "DAILY_PIPELINE")
                .withDescription("Generate daily transaction detail report (CBTRN03C)")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger transactionReportTrigger(JobDetail transactionReportJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(transactionReportJobDetail)
                .withIdentity("transactionReportTrigger", "DAILY_PIPELINE")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6 * * ?"))
                .build();
    }
}
