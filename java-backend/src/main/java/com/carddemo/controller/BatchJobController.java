package com.carddemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST API for batch job management — manual triggering and monitoring.
 * Replaces the mainframe operator console and Control-M web interface.
 *
 * Endpoints:
 *   POST /api/v1/batch/jobs/{jobName}/run   — Trigger a job manually
 *   GET  /api/v1/batch/jobs                  — List all registered jobs
 *   GET  /api/v1/batch/jobs/{jobName}/status — Get latest execution status
 *   GET  /api/v1/batch/jobs/{jobName}/history — Get execution history
 */
@RestController
@RequestMapping("/api/v1/batch")
public class BatchJobController {

    private static final Logger log = LoggerFactory.getLogger(BatchJobController.class);

    private final JobLauncher asyncJobLauncher;
    private final JobExplorer jobExplorer;
    private final Map<String, Job> jobRegistry;

    public BatchJobController(@Qualifier("asyncJobLauncher") JobLauncher asyncJobLauncher,
                              JobExplorer jobExplorer,
                              Job postTransactionJob,
                              Job interestCalculationJob,
                              Job statementGenerationJob,
                              Job transactionReportJob,
                              Job dataExportJob,
                              Job dataImportJob) {
        this.asyncJobLauncher = asyncJobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobRegistry = new HashMap<>();
        jobRegistry.put("postTransactionJob", postTransactionJob);
        jobRegistry.put("interestCalculationJob", interestCalculationJob);
        jobRegistry.put("statementGenerationJob", statementGenerationJob);
        jobRegistry.put("transactionReportJob", transactionReportJob);
        jobRegistry.put("dataExportJob", dataExportJob);
        jobRegistry.put("dataImportJob", dataImportJob);
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Map<String, Object>>> listJobs() {
        List<Map<String, Object>> jobs = new ArrayList<>();
        for (Map.Entry<String, Job> entry : jobRegistry.entrySet()) {
            Map<String, Object> jobInfo = new HashMap<>();
            jobInfo.put("name", entry.getKey());
            jobInfo.put("description", getJobDescription(entry.getKey()));
            jobInfo.put("schedule", getJobSchedule(entry.getKey()));
            jobInfo.put("cobolEquivalent", getCobolEquivalent(entry.getKey()));

            // Get latest execution status
            List<JobExecution> executions = getExecutions(entry.getKey(), 1);
            if (!executions.isEmpty()) {
                JobExecution latest = executions.get(0);
                jobInfo.put("lastStatus", latest.getStatus().toString());
                jobInfo.put("lastRunTime", toLocalDateTime(latest));
            } else {
                jobInfo.put("lastStatus", "NEVER_RUN");
                jobInfo.put("lastRunTime", null);
            }
            jobs.add(jobInfo);
        }
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/jobs/{jobName}/run")
    public ResponseEntity<Map<String, Object>> runJob(@PathVariable String jobName) {
        Job job = jobRegistry.get(jobName);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("triggeredBy", "REST_API")
                    .toJobParameters();

            JobExecution execution = asyncJobLauncher.run(job, params);
            log.info("Manually triggered job: {} (executionId: {})", jobName, execution.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("jobName", jobName);
            result.put("executionId", execution.getId());
            result.put("status", execution.getStatus().toString());
            result.put("startTime", toLocalDateTime(execution));
            result.put("message", "Job launched successfully");
            return ResponseEntity.accepted().body(result);
        } catch (Exception e) {
            log.error("Failed to launch job: {}", jobName, e);
            Map<String, Object> error = new HashMap<>();
            error.put("jobName", jobName);
            error.put("status", "LAUNCH_FAILED");
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/jobs/{jobName}/status")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable String jobName) {
        if (!jobRegistry.containsKey(jobName)) {
            return ResponseEntity.notFound().build();
        }

        List<JobExecution> executions = getExecutions(jobName, 1);
        Map<String, Object> status = new HashMap<>();
        status.put("jobName", jobName);

        if (executions.isEmpty()) {
            status.put("status", "NEVER_RUN");
            status.put("lastRunTime", null);
        } else {
            JobExecution latest = executions.get(0);
            status.put("executionId", latest.getId());
            status.put("status", latest.getStatus().toString());
            status.put("startTime", toLocalDateTime(latest));
            status.put("endTime", latest.getEndTime() != null
                    ? latest.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
            status.put("exitCode", latest.getExitStatus().getExitCode());
            status.put("exitDescription", latest.getExitStatus().getExitDescription());
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/jobs/{jobName}/history")
    public ResponseEntity<List<Map<String, Object>>> getJobHistory(@PathVariable String jobName) {
        if (!jobRegistry.containsKey(jobName)) {
            return ResponseEntity.notFound().build();
        }

        List<JobExecution> executions = getExecutions(jobName, 20);
        List<Map<String, Object>> history = new ArrayList<>();
        for (JobExecution exec : executions) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("executionId", exec.getId());
            entry.put("status", exec.getStatus().toString());
            entry.put("startTime", toLocalDateTime(exec));
            entry.put("endTime", exec.getEndTime() != null
                    ? exec.getEndTime().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
            entry.put("exitCode", exec.getExitStatus().getExitCode());
            entry.put("parameters", exec.getJobParameters().toProperties());
            history.add(entry);
        }
        return ResponseEntity.ok(history);
    }

    private List<JobExecution> getExecutions(String jobName, int maxCount) {
        List<Long> instanceIds = jobExplorer.getJobInstances(jobName, 0, maxCount)
                .stream()
                .map(instance -> instance.getInstanceId())
                .toList();

        List<JobExecution> allExecutions = new ArrayList<>();
        for (Long instanceId : instanceIds) {
            allExecutions.addAll(jobExplorer.getJobExecutions(
                    jobExplorer.getJobInstance(instanceId)));
        }
        allExecutions.sort((a, b) -> b.getStartTime().compareTo(a.getStartTime()));
        return allExecutions.stream().limit(maxCount).toList();
    }

    private LocalDateTime toLocalDateTime(JobExecution exec) {
        if (exec.getStartTime() == null) return null;
        return exec.getStartTime().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String getJobDescription(String jobName) {
        return switch (jobName) {
            case "postTransactionJob" -> "Post daily transactions to master file with validation";
            case "interestCalculationJob" -> "Calculate monthly interest on account balances";
            case "statementGenerationJob" -> "Generate monthly account statements (text + HTML)";
            case "transactionReportJob" -> "Generate daily transaction detail report";
            case "dataExportJob" -> "Export all data to pipe-delimited files";
            case "dataImportJob" -> "Import data from pipe-delimited files";
            default -> "Unknown job";
        };
    }

    private String getJobSchedule(String jobName) {
        return switch (jobName) {
            case "postTransactionJob" -> "Daily at 10:00 PM (0 0 22 * * ?)";
            case "interestCalculationJob" -> "Monthly on 1st at 2:00 AM (0 0 2 1 * ?)";
            case "statementGenerationJob" -> "Monthly on 1st at 3:00 AM (0 0 3 1 * ?)";
            case "transactionReportJob" -> "Daily at 6:00 AM (0 0 6 * * ?)";
            case "dataExportJob" -> "On-demand (manual trigger only)";
            case "dataImportJob" -> "On-demand (manual trigger only)";
            default -> "Not scheduled";
        };
    }

    private String getCobolEquivalent(String jobName) {
        return switch (jobName) {
            case "postTransactionJob" -> "POSTTRAN.jcl → CBTRN02C.cbl";
            case "interestCalculationJob" -> "INTCALC.jcl → CBACT04C.cbl";
            case "statementGenerationJob" -> "CREASTMT.jcl → CBSTM03A.CBL";
            case "transactionReportJob" -> "TRANREPT.jcl → CBTRN03C.cbl";
            case "dataExportJob" -> "EXPDATA.jcl → CBEXPORT.cbl";
            case "dataImportJob" -> "IMPDATA.jcl → CBIMPORT.cbl";
            default -> "N/A";
        };
    }
}
