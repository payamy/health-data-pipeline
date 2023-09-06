package com.payamy.health.server.service;

import com.payamy.health.server.entity.Event;
import com.payamy.health.server.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    public void sendMessage(String topic, Event data) {
        Message<Event> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        LOGGER.info(String.format("Message sent %s", message));
        kafkaTemplate.send(message);
    }
}
