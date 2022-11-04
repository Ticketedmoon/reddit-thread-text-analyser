package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LexicalExtractionService {

    private static final String STOP_WORD_FILE_PATH = "src/main/resources/data/english_stop_words.txt";
    private static final String WHITESPACE_MATCH_REGEX = "\\s+";
    private static final String PUNCTUATION_REMOVAL_REGEX = "\\p{P}";

    private List<String> stopWords;

    private final UserThreadTextRepository repository;

    @PostConstruct
    public void initialise() throws IOException {
        stopWords = Files.readAllLines(Paths.get(STOP_WORD_FILE_PATH));
    }

    /**
     * Split the payload into a list of words and save each word in the database.
     * If the word already exists, increment the count of that word.
     *
     * @param textPayloadEvent The event object containing the textual information from a post/comment/reply.
     */
    public void savePayload(TextPayloadEvent textPayloadEvent) {
        Map<String, Integer> wordToCountMap = getWordToCountMap(textPayloadEvent);
        List<UserThreadTextItem> threadTextItems = extractThreadTextItems(textPayloadEvent.getType(), wordToCountMap);
        repository.saveAll(threadTextItems);
    }

    private Map<String, Integer> getWordToCountMap(TextPayloadEvent textPayloadEvent) {
        List<String> words = convertPayloadTextToSanitisedWords(textPayloadEvent);
        Map<String, Integer> wordToCountFromPayload = new HashMap<>();
        words.forEach(word -> wordToCountFromPayload.put(word, wordToCountFromPayload.getOrDefault(word, 0) + 1));
        return wordToCountFromPayload;
    }

    private List<String> convertPayloadTextToSanitisedWords(TextPayloadEvent textPayloadEvent) {
        String cleanedPayload = textPayloadEvent.getPayload()
            .replaceAll(PUNCTUATION_REMOVAL_REGEX, "")
            .replaceAll("\n", " ");
        List<String> words = Arrays.stream(cleanedPayload.split(WHITESPACE_MATCH_REGEX))
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        words.removeAll(stopWords);
        return words;
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