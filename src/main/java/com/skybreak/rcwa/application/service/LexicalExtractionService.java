package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LexicalExtractionService {

    private static final String PUNCTUATION_REMOVAL_REGEX = "\\p{P}";

    private final UserThreadTextRepository repository;

    /**
     * Split the payload into a list of words and save each word in the database.
     * If the word already exists, increment the count of that word.
     *
     * @param textPayloadEvent The event object containing the textual information from a post/comment/reply.
     */
    public void savePayload(TextPayloadEvent textPayloadEvent) {
        Map<String, Integer> wordToCountMap = getWordToCountMap(textPayloadEvent);
        List<UserThreadTextItem> threadTextItems = extractThreadTextItems(textPayloadEvent, wordToCountMap);
        repository.saveAll(threadTextItems);
    }

    private Map<String, Integer> getWordToCountMap(TextPayloadEvent textPayloadEvent) {
        String cleanedPayload = textPayloadEvent.getPayload().replaceAll(PUNCTUATION_REMOVAL_REGEX, "");
        List<String> words = Arrays.asList(cleanedPayload.split(" "));
        Map<String, Integer> wordToCountFromPayload = new HashMap<>();
        words.stream()
            .map(String::toLowerCase)
            .forEach(word -> wordToCountFromPayload.put(word, wordToCountFromPayload.getOrDefault(word, 0) + 1));
        return wordToCountFromPayload;
    }

    private List<UserThreadTextItem> extractThreadTextItems(TextPayloadEvent textPayloadEvent, Map<String, Integer> wordToCountMap) {
        List<UserThreadTextItem> threadTextItems = new ArrayList<>();
        wordToCountMap.forEach((word, count) -> {
            UserThreadTextItem threadTextItem = repository.findByTextItem(word);
            if (threadTextItem != null) {
                threadTextItem.setCount(threadTextItem.getCount() + count);
            } else {
                threadTextItem = UserThreadTextItem.builder()
                    .textItem(word)
                    .type(textPayloadEvent.getType())
                    .count(count)
                    .build();
            }
            threadTextItems.add(threadTextItem);
        });
        return threadTextItems;
    }
}