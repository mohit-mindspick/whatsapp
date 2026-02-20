package com.assetneuron.whatsapp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Getter
public class KafkaTopicConfig {
    
    @Value("${kafka.topic.whatsapp.events:whatsapp.events}")
    private String whatsappEventsTopic;
    
    @Value("${kafka.topic.partitions:3}")
    private int partitions;
    
    @Value("${kafka.topic.replicas:1}")
    private int replicas;
        
    @Bean
    @ConditionalOnProperty(name = "event.publisher.type", havingValue = "KAFKA")
    public NewTopic whatsappEventsTopicBean() {
        log.info("Creating Kafka topic: {} with {} partitions and {} replicas", 
                whatsappEventsTopic, partitions, replicas);
        return TopicBuilder.name(whatsappEventsTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}

