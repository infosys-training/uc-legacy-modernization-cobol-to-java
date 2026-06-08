package com.carddemo.batch.quartz;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Quartz bridge for the TransactionReport Spring Batch job.
 * Triggered by cron: 0 0 6 * * ? (6 AM daily)
 * Replaces: JCL TRANREPT.jcl → CBTRN03C
 */
@Component
public class TransactionReportQuartzJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(TransactionReportQuartzJob.class);

    private final JobLauncher jobLauncher;
    private final Job transactionReportJob;

    public TransactionReportQuartzJob(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
                                      Job transactionReportJob) {
        this.jobLauncher = jobLauncher;
        this.transactionReportJob = transactionReportJob;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("triggeredBy", "QUARTZ_SCHEDULER")
                    .toJobParameters();
            log.info("Quartz triggering transactionReportJob");
            jobLauncher.run(transactionReportJob, params);
        } catch (Exception e) {
            log.error("Failed to launch transactionReportJob", e);
        }
    }
}
