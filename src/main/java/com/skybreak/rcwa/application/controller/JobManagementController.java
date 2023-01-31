package com.skybreak.rcwa.application.controller;

import com.skybreak.rcwa.application.dto.JobCreated;
import com.skybreak.rcwa.application.service.JobManagementService;
import com.skybreak.rcwa.domain.core.JobResultSummary;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class JobManagementController {

    private static final String JOB_STARTED_MESSAGE = "Word Analysis job started for subreddit [%s] - scanning from the top [%d] posts";
    private static final String TOTAL_POSTS_VALIDATION_EXCEPTION_MESSAGE = "parameter `totalPosts` must be less than or equal to 100";
    private final JobManagementService jobManagementService;

    @PostMapping("/job-reports")
    public ResponseEntity<JobCreated> startReport(@RequestParam String subreddit,
                                                  @RequestParam(defaultValue = "25")
                                                  @Max(value = 100, message = TOTAL_POSTS_VALIDATION_EXCEPTION_MESSAGE) int totalPosts) {
        UUID jobId = UUID.randomUUID();
        log.info("Starting word scan job with ID: {} for subreddit: {}. total posts to scan: {}", jobId, subreddit, totalPosts);

        CompletableFuture.runAsync(() -> {
            jobManagementService.startJob(jobId, subreddit, totalPosts);
            log.info("Job ID {} finished publishing text items for subreddit: {}, total posts: {}", jobId, subreddit, totalPosts);
        });

        return ResponseEntity.accepted().body(
            JobCreated.builder()
                .jobId(jobId)
                .message(JOB_STARTED_MESSAGE.formatted(subreddit, totalPosts))
                .status(HttpStatus.ACCEPTED.value())
                .build()
        );
    }

    @GetMapping("/job-reports/results")
    public ResponseEntity<List<JobExecutionMetadata>> getJobs() {
        List<JobExecutionMetadata> jobResultSummaryList = jobManagementService.getResultSummariesForAllJobs();
        return ResponseEntity.ok(jobResultSummaryList);
    }

    @GetMapping("/job-reports/results/{jobId}")
    public ResponseEntity<JobResultSummary> getJobById(@PathVariable UUID jobId) {
        JobResultSummary resultSummaryForJob = jobManagementService.getResultsForJob(jobId);
        return ResponseEntity.ok(resultSummaryForJob);
    }
}