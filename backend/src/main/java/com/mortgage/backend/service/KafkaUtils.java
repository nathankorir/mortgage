package com.mortgage.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.backend.dto.KafkaMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaUtils {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(KafkaUtils.class);
    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public KafkaUtils(KafkaTemplate kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produceKafkaMessage(KafkaMessageDto kafkaMessageDto) {
//        Map<String, String> messageMap = new HashMap<>();
//        messageMap.put(String.valueOf(kafkaMessageDto.getApplication().getId()), kafkaMessageDto.toString());
//        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, messageMap.toString());
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                logger.info("Sent message to kafka");
//            } else {
//                logger.error("Failed to send message " + ex.getMessage());
//            }
//        });

        try {
            String key = String.valueOf(kafkaMessageDto.getApplication().getId());
            String message = objectMapper.writeValueAsString(kafkaMessageDto);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("✅ Sent message to Kafka: {}", message);
                } else {
                    logger.error("❌ Failed to send message to Kafka", ex);
                }
            });

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize KafkaMessageDto", e);
            throw new RuntimeException("Kafka message serialization failed", e); // optional, triggers 500 if not handled
        }
    }
}
