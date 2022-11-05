package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.AbstractTestContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordCountServiceIT extends AbstractTestContainer {

    @Autowired
    private WordCountService wordCountService;

    @Test
    void givenTextPayloadEvent_whenConsumed_thenShouldBuildMapOfEachWordAndCount() {
        String data = "Hello, World! Always remember: \"Hello, World!\"";
        Map<String, Integer> wordToCountMap = wordCountService.getWordToCountMapForTextPayload(data);
        Assertions.assertEquals(4, wordToCountMap.size());
        Assertions.assertEquals(1, wordToCountMap.get("always"));
        Assertions.assertEquals(1, wordToCountMap.get("remember"));
        Assertions.assertEquals(2, wordToCountMap.get("hello"));
        Assertions.assertEquals(2, wordToCountMap.get("world"));
    }

    @Test
    void givenTextPayloadEventWithStopWords_whenConsumed_thenShouldRemoveStopWordsAndBuildMapOfRemainingWordsAndCounts() {
        String data = "Hello, World! I only will now say: \"Hello, World!\"";
        Map<String, Integer> wordToCountMap = wordCountService.getWordToCountMapForTextPayload(data);
        Assertions.assertEquals(3, wordToCountMap.size());
        Assertions.assertEquals(1, wordToCountMap.get("say"));
        Assertions.assertEquals(2, wordToCountMap.get("hello"));
        Assertions.assertEquals(2, wordToCountMap.get("world"));
    }
}