package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.core.JobResultSummary;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;

import java.util.List;
import java.util.UUID;

public interface JobManagementService {

    /**
     * Start the text analysis job.
     * This will scan the top <code>totalPosts</code> total posts from a particular subreddit.
     * Words will be counted across Post text and comment text.
     * This will give us an idea the most common words per each subreddit community.
     * Job metadata will be stored.
     *
     * @param subreddit The subreddit we are analysing.
     * @param totalPostsToScan The amount of top posts to scan.
     */
    void startJob(UUID jobId, String subreddit, int totalPostsToScan);

    /**
     * Retrieve the summary metadata for all jobs.
     *
     */
    List<JobExecutionMetadata> getResultSummariesForAllJobs();

    /**
     * Retrieve the metadata and results for a subreddit analysis job.
     * Results will be sorted by word-count in descending order (desc).
     *
     * @param jobId The jobId associated with a particular subreddit word analysis.
     */
    JobResultSummary getResultsForJob(UUID jobId);
}
