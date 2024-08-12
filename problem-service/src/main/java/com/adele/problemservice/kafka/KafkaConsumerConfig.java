package com.adele.problemservice.kafka;

import com.adele.problemservice.dto.KafkaCompile;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka-url}")
    private String kafkaUrl;

    @Bean
    public ConsumerFactory<String, KafkaCompile> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "problem_service_group_1");
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),new JsonDeserializer<>(KafkaCompile.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaCompile> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaCompile> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
