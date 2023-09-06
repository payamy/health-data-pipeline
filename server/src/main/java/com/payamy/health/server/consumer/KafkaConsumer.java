package com.payamy.health.server.consumer;

import com.payamy.health.server.entity.Event;
import com.payamy.health.server.entity.User;
import com.payamy.health.server.producer.KafkaProducer;
import com.payamy.health.server.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @Autowired
    private KafkaProducer kafkaProducer;

    @KafkaListener(topics = {"heartbeat", "movement"}, groupId = "etlGroup")
    public void aggregateTopics(Event event) {
        Long userId = event.getUserId();
        kafkaProducer.sendMessage(userId, event);
    }
}
