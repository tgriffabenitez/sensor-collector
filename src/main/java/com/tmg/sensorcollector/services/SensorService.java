package com.tmg.sensorcollector.services;

import com.tmg.sensorcollector.handlers.KafkaHandler;
import com.tmg.sensorcollector.models.SensorInformation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SensorService {
    private static final String SENSOR_DATA_TOPIC = "sensorData";

    private final KafkaHandler kafkaHandler;

    @Autowired
    public SensorService(KafkaHandler kafkaHandler) {
        this.kafkaHandler = kafkaHandler;
    }

    public void sendToKafka(SensorInformation sensorInformation) {
        log.warn("Sending Sensor Information to Kafka: {}", sensorInformation);
        kafkaHandler.sendToKafka(SENSOR_DATA_TOPIC, sensorInformation.toString());
    }
}
