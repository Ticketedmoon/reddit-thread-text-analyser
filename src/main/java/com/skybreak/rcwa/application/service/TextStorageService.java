package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextStorageService {

    private final WordCountService wordCountService;
    private final UserThreadTextRepository repository;

    /**
     * Split the payload into a map of cleaned/sanitised word items with word count.
     * Build a list of words to add or update in the database with updated word counts.
     *
     * @param event The textual payload event from a post/comment/reply.
     */
    public void savePayload(TextPayloadEvent event) {
        Map<String, Integer> wordToCountMap = wordCountService.getWordToCountMapForTextPayload(event.getPayload());
        List<UserThreadTextItem> textItems = extractThreadTextItems(event.getType(), wordToCountMap);
        repository.saveAll(textItems);
    }

    /**
     * Called when the final textual event is consumed.
     * Report on the results of the scan.
     *
     * @param payload The textual payload from a post/comment/reply.
     */
    public void completeAnalysisJob(String payload) {
        log.info("Subreddit [{}] analysis job completed.", payload);
        // TODO Do any reporting at this stage.
    }

    private List<UserThreadTextItem> extractThreadTextItems(TextPayloadEventType eventType, Map<String, Integer> wordToCountMap) {
        List<UserThreadTextItem> threadTextItems = new ArrayList<>();
        wordToCountMap.forEach((word, count) -> {
            UserThreadTextItem threadTextItem = repository.findByTypeAndTextItem(eventType, word);
            if (threadTextItem != null) {
                threadTextItem.setCount(threadTextItem.getCount() + count);
            } else {
                threadTextItem = UserThreadTextItem.builder()
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
