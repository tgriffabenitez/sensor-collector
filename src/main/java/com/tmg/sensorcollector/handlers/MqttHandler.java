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
import reactor.util.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Log4j2
public class MqttHandler implements MessageHandler {

    private static final Integer MAX_THREADS = 10;

    private final ObjectMapper objectMapper;
    private final SensorService sensorService;
    private final ExecutorService executorService;

    @Autowired
    public MqttHandler(ObjectMapper objectMapper, SensorService sensorService) {
        this.objectMapper = objectMapper;
        this.sensorService = sensorService;
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    @Override
    public void handleMessage(@NonNull Message<?> message) throws MessagingException {
        executorService.submit(() -> processMessageAsync(message));
    }

    private void processMessageAsync(Message<?> message) {
        log.info("Mensaje MQTT recibido en el hilo: {}", Thread.currentThread().getName());

        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();

        if (topic == null || topic.isEmpty()) {
            log.warn("El topic del mensaje está vacío");
            return;
        }

        if (payload.isEmpty()) {
            log.warn("El payload del mensaje está vacío");
            return;
        }

        log.debug("Topic: {} - Payload recibido: {}", topic, payload);

        SensorInformation sensorInformation = processPayload(payload);
        if (sensorInformation == null) {
            log.warn("La información del sensor es null, no se enviará a Kafka.");
            return;
        }

        log.trace("Enviando sensorInformation a kafka");
        sensorService.sendToKafka(sensorInformation);
    }

    private SensorInformation processPayload(String payload) {
        try {
            log.trace("Procesando mensaje");
            return objectMapper.readValue(payload, SensorInformation.class);
        } catch (JsonProcessingException e) {
            log.error("Error al procesar el payload: {}", e.getMessage());
            return null;
        }
    }
}
