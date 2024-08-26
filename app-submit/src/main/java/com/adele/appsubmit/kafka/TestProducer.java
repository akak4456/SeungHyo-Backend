package com.adele.appsubmit.kafka;

import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.dto.KafkaCompile;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestProducer {
    private final KafkaTemplate<String, KafkaCompile> kafkaTemplate;

    public void create() {
        kafkaTemplate.send("submit.1", new KafkaCompile(CompileStatus.CORRECT, 2L, "", "", null,null));
    }
}
