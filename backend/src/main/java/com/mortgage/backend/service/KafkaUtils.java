package com.mortgage.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaUtils {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public KafkaUtils(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceKafkaMessage(String key, String message) {
        logger.info("Producing Kafka message for key: " + key + " and message: " + message);
        kafkaTemplate.send(topic, key, message);
    }
}
