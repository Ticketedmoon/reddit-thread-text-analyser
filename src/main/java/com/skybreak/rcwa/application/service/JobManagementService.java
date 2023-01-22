package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.core.JobResultSummary;
import com.skybreak.rcwa.infrastructure.persistence.JobExecutionMetadataRepository;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobManagementService {

    private final DataExtractionProducer dataExtractionProducer;
    private final JobExecutionMetadataRepository jobExecutionMetadataRepository;
    private final UserThreadTextRepository userThreadTextRepository;

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
    public void startJob(UUID jobId, String subreddit, int totalPostsToScan) {
        try {
            JobExecutionMetadata jobExecutionMetadata = JobExecutionMetadata.builder()
                .id(jobId)
                .subreddit(subreddit)
                .jobStartTime(LocalDateTime.now())
                .build();
            jobExecutionMetadataRepository.save(jobExecutionMetadata);
            dataExtractionProducer.startTextExtraction(jobId, subreddit, totalPostsToScan);
        } catch (AuthenticationException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to connect to Reddit API", e);
        }
    }

    /**
     * Retrieve the metadata and results for a subreddit analysis job.
     * Results will be sorted by word-count in descending order (desc).
     *
     * @param jobId The jobId associated with a particular subreddit word analysis.
     */
    public JobResultSummary getResultsForJob(UUID jobId) {
        JobExecutionMetadata jobResultSummary = jobExecutionMetadataRepository.findById(jobId);
        List<UserThreadTextItem> results = userThreadTextRepository.findAllByJobId(jobId)
            .stream()
            .sorted(Comparator.comparingInt(UserThreadTextItem::getCount).reversed())
            .collect(Collectors.toList());
        return JobResultSummary.builder()
            .jobExecutionMetadata(jobResultSummary)
            .results(results)
            .build();
    }
}
