package com.adele.problemservice.kafka;

import com.adele.problemservice.kafka.dto.KafkaCompile;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestProducer {
    private final KafkaTemplate<String, KafkaCompile> kafkaTemplate;

    public void create() {
        kafkaTemplate.send("topic", new KafkaCompile("say hello~"));
    }
}
