package com.tmg.sensorcollector.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmg.sensorcollector.models.SensorInformation;
import com.tmg.sensorcollector.services.SensorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MqttHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final SensorService sensorService;

    @Autowired
    public MqttHandler(ObjectMapper objectMapper, SensorService sensorService) {
        this.objectMapper = objectMapper;
        this.sensorService = sensorService;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        log.info("Mensaje MQTT recibido");

        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();

        if (topic == null || topic.isEmpty()) {
            log.warn("El topic del mensaje está vacío");
            return;
        }

        log.info("Topic recibido: {}", topic);

        if (payload.isEmpty()) {
            log.warn("El payload del mensaje está vacío");
            return;
        }

        log.info("Payload recibido: {}", payload);

        SensorInformation sensorInformation = processPayload(payload);
        if (sensorInformation == null) {
            log.warn("La información del sensor es null, no se enviará a Kafka.");
            return;
        }

        sensorService.sendToKafka(sensorInformation);
    }

    private SensorInformation processPayload(String payload) {
        try {
            return objectMapper.readValue(payload, SensorInformation.class);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar el payload: {}", e.getMessage());
            return null;
        }
    }
}
