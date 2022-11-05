package com.skybreak.rcwa.application.consumer;

import com.skybreak.rcwa.application.service.TextStorageService;
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

    private final TextStorageService textStorageService;

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload TextPayloadEvent event) {
        if (event.getType() == TextPayloadEventType.COMPLETION) {
            textStorageService.completeAnalysisJob(event.getPayload());
        } else {
            log.info("Consuming message of [type, payload]: [{}, {}]", event.getType(), event.getPayload());
            textStorageService.savePayload(event);
        }
    }
}