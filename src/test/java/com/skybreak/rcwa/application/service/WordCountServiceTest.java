package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.AbstractTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WordCountServiceTest extends AbstractTestContainer {

    private static final String DEFAULT_PAYLOAD =
        """
        Springboot is a Dependency Injection Framework for building Java applications.
        Spring Dependency Injection is the concept where by objects are managed by an 'application container' or
        'spring context', and are injected into your objects at runtime. Some other dependency injection frameworks
        such as 'dagger'.
        """;

    @InjectMocks
    private WordCountService target;

    @BeforeEach
    void setUp() throws IOException {
        target.initialise();
    }

    @Test
    void getWordToCountMapForTextPayload_whenExecutedWithPayload_shouldCountWords() {
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(DEFAULT_PAYLOAD);
        assertThat(wordsToCount.get("dependency")).isEqualTo(3);
        assertThat(wordsToCount.get("spring")).isEqualTo(2);
        assertThat(wordsToCount.get("objects")).isEqualTo(2);
    }

    @Test
    void getWordToCountMapForTextPayload_whenPayloadHasPunctuation_shouldClean() {
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(DEFAULT_PAYLOAD);
        wordsToCount.keySet().forEach(word -> assertThat(word)
            .doesNotContain("'")
            .doesNotContain(".")
            .doesNotContain(","));
    }

    @Test
    void getWordToCountMapForTextPayload_whenPayloadHasHyperlink_shouldClean() {
        String testPayload =
            """
            Reddit hyperlink contained: https://www.reddit-community-word-analyser.test.com/verify/1/test
            """;
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(testPayload);

        Set<String> words = wordsToCount.keySet();
        assertThat(words.size()).isEqualTo(3);
        assertThat(words).contains("reddit");
        assertThat(words).contains("hyperlink");
        assertThat(words).contains("contained");
    }

    @Test
    void getWordToCountMapForTextPayload_whenPayloadHasNewLineCharacters_shouldClean() {
        String testPayload = "Thread\nText\nContaining\nWords\n";
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(testPayload);

        Set<String> words = wordsToCount.keySet();
        assertThat(words.size()).isEqualTo(4);
        assertThat(words).contains("thread");
        assertThat(words).contains("text");
        assertThat(words).contains("containing");
        assertThat(words).contains("words");
    }

    @Test
    void getWordToCountMapForTextPayload_whenPayloadHasForbiddenStopWords_shouldClean() {
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(DEFAULT_PAYLOAD);

        Set<String> words = wordsToCount.keySet();
        assertThat(words).doesNotContain("the");
        assertThat(words).doesNotContain("is");
        assertThat(words).doesNotContain("a");
        assertThat(words).doesNotContain("for");

        String[] defaultPayloadSplitItems = DEFAULT_PAYLOAD.split(" ");
        assertThat(defaultPayloadSplitItems.length).isEqualTo(41);
        assertThat(words.size()).isEqualTo(18);
    }

    @Test
    void getWordToCountMapForTextPayload_whenReturned_shouldHaveAllWordsLowercase() {
        Map<String, Integer> wordsToCount = target.getWordToCountMapForTextPayload(DEFAULT_PAYLOAD);
        Set<String> words = wordsToCount.keySet();
        words.forEach(word -> assertThat(word).isEqualTo(word.toLowerCase()));
    }

}