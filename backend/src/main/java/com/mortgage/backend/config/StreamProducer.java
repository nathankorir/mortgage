package com.mortgage.backend.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(StreamProducer.class);

    public void produce(String topic, String key, String message) {
        try {
            kafkaTemplate.send(topic, key, message);
        } catch (Exception e) {
            logger.error("Failed to send Kafka message to topic {}: {}", topic, e.getMessage(), e);
        }
    }
}
