package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.JobExecutionMetadataRepository;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.JobExecutionMetadata;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextStorageService {


    private static final String FAILED_TO_FIND_JOB_FOR_COMPLETION_ERROR = "Failed to find/complete job with ID %s, job finish time not set";

    private final WordCountService wordCountService;
    private final UserThreadTextRepository repository;
    private final JobExecutionMetadataRepository jobExecutionMetadataRepository;

    /**
     * Split the payload into a map of cleaned/sanitised word items with word count.
     * Build a list of words to add or update in the database with updated word counts.
     *
     * @param event The textual payload event from a post/comment/reply.
     */
    public void savePayload(TextPayloadEvent event) {
        Map<String, Integer> wordToCountMap = wordCountService.getWordToCountMapForTextPayload(event.getPayload());
        List<UserThreadTextItem> textItems = extractThreadTextItems(event.getJobId(), event.getType(), wordToCountMap);
        repository.saveAll(textItems);
    }

    /**
     * Called when the final textual event is consumed.
     * Report on the results of the scan.
     *
     * @param event The textual payload from a post/comment/reply.
     */
    public void completeAnalysisJob(TextPayloadEvent event) {
        log.info("Subreddit [{}] analysis job completed.", event.getPayload());
        JobExecutionMetadata summaryInfo = jobExecutionMetadataRepository.findById(event.getJobId())
            .orElseThrow(() -> new IllegalStateException(FAILED_TO_FIND_JOB_FOR_COMPLETION_ERROR.formatted(event.getJobId())));
        summaryInfo.setJobFinishTime(LocalDateTime.now());
        jobExecutionMetadataRepository.save(summaryInfo);
    }

    private List<UserThreadTextItem> extractThreadTextItems(UUID jobId,
                                                            TextPayloadEventType eventType,
                                                            Map<String, Integer> wordToCountMap) {
        List<UserThreadTextItem> threadTextItems = new ArrayList<>();
        wordToCountMap.forEach((word, count) -> {
            UserThreadTextItem threadTextItem = repository.findByJobIdAndTypeAndTextItem(jobId, eventType, word);
            if (threadTextItem != null) {
                threadTextItem.setCount(threadTextItem.getCount() + count);
            } else {
                threadTextItem = UserThreadTextItem.builder()
                    .jobId(jobId)
                    .textItem(word)
                    .type(eventType)
                    .count(count)
                    .build();
            }
            threadTextItems.add(threadTextItem);
        });
        return threadTextItems;
    }
}
