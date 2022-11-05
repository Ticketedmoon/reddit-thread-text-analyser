package com.skybreak.rcwa.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordCountService {

    private static final String STOP_WORD_FILE_PATH = "src/main/resources/data/english_stop_words.txt";
    private static final String WHITESPACE_MATCH_REGEX = "\\s+";
    private static final String PUNCTUATION_REMOVAL_REGEX = "[^a-zA-Z0-9\\s+]";

    private List<String> stopWords;

    @PostConstruct
    private void initialise() throws IOException {
        stopWords = Files.readAllLines(Paths.get(STOP_WORD_FILE_PATH));
    }

    /**
     * Split the payload into a map of cleaned/sanitised word items with word count.
     * If the word has multiple occurrences, increment the count of that word that many times.
     *
     * @param payload The textual data from a post/comment/reply.
     * @return A map for all cleaned words from the payload to the count,
     *         e.g., {"hello": 1, "darkness": 2, "old": 1, "friend": 3}
     */
    public Map<String, Integer> getWordToCountMapForTextPayload(String payload) {
        List<String> words = convertPayloadTextToSanitisedWords(payload);
        Map<String, Integer> wordToCountFromPayload = new HashMap<>();
        words.forEach(word -> wordToCountFromPayload.put(word, wordToCountFromPayload.getOrDefault(word, 0) + 1));
        return wordToCountFromPayload;
    }

    private List<String> convertPayloadTextToSanitisedWords(String payload) {
        String cleanedPayload = payload
            .replaceAll("\n", " ")
            .replaceAll(PUNCTUATION_REMOVAL_REGEX, "");
        List<String> words = Arrays.stream(cleanedPayload.split(WHITESPACE_MATCH_REGEX))
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        words.removeAll(stopWords);
        words.removeIf(item -> item.length() < 3);
        return words;
    }
}