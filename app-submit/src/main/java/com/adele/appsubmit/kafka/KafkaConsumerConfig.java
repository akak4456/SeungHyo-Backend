package com.adele.appsubmit.kafka;

import com.adele.appsubmit.properties.KafkaConfigProperties;
import com.adele.domainproblem.dto.KafkaCompile;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String kafkaUrl;

    public KafkaConsumerConfig(@Autowired KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaUrl = kafkaConfigProperties.getUrl();
    }

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
