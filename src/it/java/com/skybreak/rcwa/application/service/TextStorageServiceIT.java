package com.skybreak.rcwa.application.service;

import com.skybreak.rcwa.AbstractTestContainer;
import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import com.skybreak.rcwa.infrastructure.persistence.UserThreadTextRepository;
import com.skybreak.rcwa.infrastructure.persistence.dao.UserThreadTextItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TextStorageServiceIT extends AbstractTestContainer {

    @Autowired
    private UserThreadTextRepository repository;

    @Autowired
    private TextStorageService textStorageService;

    @AfterEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void givenTextPayloadEvent_whenConsumed_thenShouldStoreEachWordAndCount() {
        String data = "Hello, World! Always remember: \"Hello, World!\"";
        TextPayloadEvent payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.POST)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);
        Iterable<UserThreadTextItem> textItemIterable = repository.findAll();
        Map<String, UserThreadTextItem> textLabelToItem = StreamSupport.stream(textItemIterable.spliterator(), false).toList()
            .stream()
            .collect(Collectors.toMap(UserThreadTextItem::getTextItem, Function.identity()));
        Assertions.assertEquals(4, textLabelToItem.size());
        Assertions.assertEquals(1, textLabelToItem.get("always").getCount());
        Assertions.assertEquals(2, textLabelToItem.get("hello").getCount());
        Assertions.assertEquals(2, textLabelToItem.get("world").getCount());
    }

    @Test
    void givenTwoTextPayloadEventsOfSameType_whenConsumed_thenShouldStoreEachWordAndCount() {
        String data = "Hello, World! Always remember: \"Hello, World!\"";
        TextPayloadEvent payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.POST)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);

        data = "Hello, World!";
        payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.POST)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);

        Iterable<UserThreadTextItem> textItemIterable = repository.findAll();
        Map<String, UserThreadTextItem> textLabelToItem = StreamSupport.stream(textItemIterable.spliterator(), false).toList()
            .stream()
            .collect(Collectors.toMap(UserThreadTextItem::getTextItem, Function.identity()));
        Assertions.assertEquals(4, textLabelToItem.size());
        Assertions.assertEquals(1, textLabelToItem.get("always").getCount());
        Assertions.assertEquals(3, textLabelToItem.get("hello").getCount());
        Assertions.assertEquals(3, textLabelToItem.get("world").getCount());
        Assertions.assertTrue(textLabelToItem.values().stream().allMatch(item -> item.getType() == TextPayloadEventType.POST));
    }

    @Test
    void givenTwoTextPayloadEventsOfDifferentType_whenConsumed_thenShouldStoreEachWordAndCountSeparately() {
        String data = "Hello, World! Always remember say: \"Hello, World!\"";
        TextPayloadEvent payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.POST)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);

        data = "Hello, World!";
        payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.COMMENT)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);

        Iterable<UserThreadTextItem> textItemIterable = repository.findAll();
        Map<TextPayloadEventType, List<UserThreadTextItem>> textLabelToItem = StreamSupport.stream(textItemIterable.spliterator(), false).toList()
            .stream()
            .collect(Collectors.groupingBy(UserThreadTextItem::getType));
        Assertions.assertEquals(2, textLabelToItem.size());
        Assertions.assertEquals(5, textLabelToItem.get(TextPayloadEventType.POST).size());
        Assertions.assertEquals(2, textLabelToItem.get(TextPayloadEventType.COMMENT).size());
    }

    @Test
    void givenTextPayloadEventWithStopWords_whenConsumed_thenShouldRemoveStopWordsAndStoreRemainingWords() {
        String data = "Hello, World! I only will now say: \"Hello, World!\"";
        TextPayloadEvent payloadEvent = TextPayloadEvent.builder()
            .type(TextPayloadEventType.POST)
            .payload(data)
            .build();
        textStorageService.savePayload(payloadEvent);
        Iterable<UserThreadTextItem> textItemIterable = repository.findAll();
        Map<String, UserThreadTextItem> textLabelToItem = StreamSupport.stream(textItemIterable.spliterator(), false).toList()
            .stream()
            .collect(Collectors.toMap(UserThreadTextItem::getTextItem, Function.identity()));
        Assertions.assertEquals(3, textLabelToItem.size());
        Assertions.assertEquals(1, textLabelToItem.get("say").getCount());
        Assertions.assertEquals(2, textLabelToItem.get("hello").getCount());
        Assertions.assertEquals(2, textLabelToItem.get("world").getCount());
    }
}