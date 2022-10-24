package com.skybreak.rcwa.application.consumer;

import com.skybreak.rcwa.domain.event.TextPayloadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageQueueConsumer {

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(@Payload TextPayloadEvent event) {
        log.info("{}: {}", event.getType(), event.getPayload());
    }
}