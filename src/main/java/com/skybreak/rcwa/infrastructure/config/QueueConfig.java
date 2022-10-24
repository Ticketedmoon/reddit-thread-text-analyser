package com.skybreak.rcwa.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    public Queue myQueue(@Value("${queue.name}") String queueName) {
        // Non-durable so when rabbit is stopped, the queue is automatically removed.
        return new Queue(queueName, false);
    }
}
