package com.adele.appsubmit.kafka;

import com.adele.appsubmit.properties.KafkaConfigProperties;
import com.adele.domainproblem.dto.KafkaCompile;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    private final String kafkaUrl;
    public KafkaProducerConfig(@Autowired KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaUrl = kafkaConfigProperties.getUrl();
    }
    @Bean
    public ProducerFactory<String, KafkaCompile> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, KafkaCompile> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}