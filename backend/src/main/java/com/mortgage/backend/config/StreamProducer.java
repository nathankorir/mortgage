package com.mortgage.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void produce(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }
}
