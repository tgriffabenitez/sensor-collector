package com.tmg.sensorcollector.configurations;

import com.tmg.sensorcollector.handlers.MqttHandler;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfiguration {

    @Value("${spring.mqtt.host}")
    private String mqttHost;
    @Value("${spring.mqtt.port}")
    private String mqttPort;
    @Value("${spring.mqtt.username}")
    private String mqttUsername;
    @Value("${spring.mqtt.password}")
    private String mqttPassword;
    @Value("${spring.mqtt.generalTopic}")
    private String defaultTopic;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{String.format("tcp://%s:%s", mqttHost, mqttPort)});
        options.setUserName(mqttUsername);
        options.setPassword(mqttPassword.toCharArray());
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(MqttPahoClientFactory mqttClientFactory) {
        return new MqttPahoMessageDrivenChannelAdapter(MqttAsyncClient.generateClientId(), mqttClientFactory, defaultTopic);
    }

    @Bean
    public IntegrationFlow mqttInboundFlow(MqttPahoMessageDrivenChannelAdapter mqttInboundAdapter, MqttHandler mqttHandler) {
        return IntegrationFlow.from(mqttInboundAdapter).handle(mqttHandler).get();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutBound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}
