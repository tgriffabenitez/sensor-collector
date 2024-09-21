package com.tmg.sensorcollector.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaHandler(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToKafka(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
