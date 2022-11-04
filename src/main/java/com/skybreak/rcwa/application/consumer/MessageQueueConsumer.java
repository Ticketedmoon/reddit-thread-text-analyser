package com.skybreak.rcwa.application.consumer;

import com.skybreak.rcwa.application.service.LexicalExtractionService;
import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import com.skybreak.rcwa.domain.event.TextPayloadEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueConsumer {

    private final LexicalExtractionService lexicalExtractionService;

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload TextPayloadEvent event) {
        log.info("{}: {}", event.getType(), event.getPayload());
        lexicalExtractionService.savePayload(event);

        if (event.getType() == TextPayloadEventType.COMPLETION) {
            log.info("Subreddit [{}] analysis job completed.", event.getPayload());
            // TODO Do any reporting at this stage...
        }
    }
}