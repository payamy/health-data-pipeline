package com.payamy.health.server.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic heartbeatTopic() {
        return TopicBuilder.name("heartbeat")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic movementTopic() {
        return TopicBuilder.name("movement")
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aggregatedTopic() {
        return TopicBuilder.name("aggregated")
                .partitions(4)
                .replicas(1)
                .build();
    }
}

