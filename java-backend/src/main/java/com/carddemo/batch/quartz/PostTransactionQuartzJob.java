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
 * Quartz bridge for the PostTransaction Spring Batch job.
 * Triggered by cron: 0 0 22 * * ? (10 PM daily)
 * Replaces: JCL POSTTRAN.jcl → CBTRN02C
 */
@Component
public class PostTransactionQuartzJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(PostTransactionQuartzJob.class);

    private final JobLauncher jobLauncher;
    private final Job postTransactionJob;

    public PostTransactionQuartzJob(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
                                    Job postTransactionJob) {
        this.jobLauncher = jobLauncher;
        this.postTransactionJob = postTransactionJob;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("triggeredBy", "QUARTZ_SCHEDULER")
                    .toJobParameters();
            log.info("Quartz triggering postTransactionJob");
            jobLauncher.run(postTransactionJob, params);
        } catch (Exception e) {
            log.error("Failed to launch postTransactionJob", e);
        }
    }
}
