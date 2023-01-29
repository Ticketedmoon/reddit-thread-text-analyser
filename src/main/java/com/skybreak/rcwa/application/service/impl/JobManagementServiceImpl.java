package com.skybreak.rcwa.application.service.impl;

import com.skybreak.rcwa.application.service.JobManagementService;
import com.skybreak.rcwa.application.service.impl.producer.ThreadTextProducer;
import com.skybreak.rcwa.domain.core.JobResultSummary;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.JobExecutionMetadataRepository;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class JobManagementServiceImpl implements JobManagementService {

    private final ThreadTextProducer threadTextProducer;
    private final JobExecutionMetadataRepository jobExecutionMetadataRepository;
    private final UserThreadTextRepository userThreadTextRepository;

    @Override
    public void startJob(UUID jobId, String subreddit, int totalPostsToScan) {
        try {
            JobExecutionMetadata jobExecutionMetadata = JobExecutionMetadata.builder()
                .id(jobId)
                .subreddit(subreddit)
                .totalPostsToScan(totalPostsToScan)
                .jobStartTime(LocalDateTime.now())
                .build();
            jobExecutionMetadataRepository.save(jobExecutionMetadata);
            threadTextProducer.startTextExtraction(jobId, subreddit, totalPostsToScan);
        } catch (AuthenticationException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to connect to Reddit API", e);
        }
    }

    @Override
    public List<JobExecutionMetadata> getResultSummariesForAllJobs() {
        Iterable<JobExecutionMetadata> iterable = jobExecutionMetadataRepository.findAll();
        return StreamSupport
            .stream(iterable.spliterator(), false)
            .sorted(Comparator.comparing(JobExecutionMetadata::getJobStartTime).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public JobResultSummary getResultsForJob(UUID jobId) {

        // TODO This shouldn't be necessary anymore, only return results
        JobExecutionMetadata jobResultSummary = jobExecutionMetadataRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Job execution metadata for Job ID %s not found".formatted(jobId)));

        List<UserThreadTextItem> threadTextItems = userThreadTextRepository.findAllByJobId(jobId);
        if (threadTextItems.isEmpty()) {
            throw new IllegalArgumentException("Job results for Job ID %s not found".formatted(jobId));
        }

        Map<TextPayloadEventType, List<UserThreadTextItem>> textItemTypeToItemList = new HashMap<>();
        Map<String, UserThreadTextItem> textValueToThreadItem = new HashMap<>();

        threadTextItems.stream()
            .sorted(Comparator.comparingInt(UserThreadTextItem::getCount).reversed())
            .forEach(item -> {
                List<UserThreadTextItem> itemsForType = textItemTypeToItemList.getOrDefault(item.getType(), new ArrayList<>());
                itemsForType.add(item);

                textItemTypeToItemList.put(item.getType(), itemsForType);
                UserThreadTextItem nextItemForOverallSummary = textValueToThreadItem.getOrDefault(item.getTextItem(), UserThreadTextItem.builder()
                    .type(TextPayloadEventType.OVERALL)
                    .textItem(item.getTextItem())
                    .build());
                nextItemForOverallSummary.setCount(nextItemForOverallSummary.getCount() + item.getCount());
                textValueToThreadItem.put(item.getTextItem(), nextItemForOverallSummary);
            });

        return JobResultSummary.builder()
            .jobExecutionMetadata(jobResultSummary)
            .results(Map.of(
                TextPayloadEventType.POST, textItemTypeToItemList.get(TextPayloadEventType.POST),
                TextPayloadEventType.COMMENT, textItemTypeToItemList.get(TextPayloadEventType.COMMENT),
                TextPayloadEventType.REPLY, textItemTypeToItemList.get(TextPayloadEventType.REPLY),
                TextPayloadEventType.OVERALL, textValueToThreadItem.values().stream().toList()
            ))
            .build();
    }
}
