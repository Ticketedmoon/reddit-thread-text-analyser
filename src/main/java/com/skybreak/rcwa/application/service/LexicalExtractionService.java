package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        wordToCountMap.forEach((word, count) -> {
            UserThreadTextItem threadTextItem = UserThreadTextItem.builder()
                    .textItem(word)
                    .type(textPayloadEvent.getType())
                    .count(count)
                    .build();
            repository.save(threadTextItem);
        });
    }

    private Map<String, Integer> getWordToCountMap(TextPayloadEvent textPayloadEvent) {
        String cleanedPayload = textPayloadEvent.getPayload().replaceAll(PUNCTUATION_REMOVAL_REGEX, "");
        List<String> words = Arrays.asList(cleanedPayload.split(" "));
        Map<String, Integer> wordToCountFromPayload = new HashMap<>();
        words.forEach(word -> wordToCountFromPayload.put(word, wordToCountFromPayload.getOrDefault(word, 0)));
        return wordToCountFromPayload;
    }

}
