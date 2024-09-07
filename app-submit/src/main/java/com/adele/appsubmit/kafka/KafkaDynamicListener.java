package com.adele.appsubmit.kafka;

import com.adele.domainproblem.dto.KafkaCompile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDynamicListener {
    private final ConcurrentKafkaListenerContainerFactory<String, KafkaCompile> kafkaListenerContainerFactory;
    public ConcurrentMessageListenerContainer<String, KafkaCompile> addDynamicTopicListener(String submitNo, Consumer<KafkaCompile> consumer) {
        log.info("addDynamicTopicListener called");
        try {
            ConcurrentMessageListenerContainer<String, KafkaCompile> container
                    = kafkaListenerContainerFactory
                    .createContainer("submit." + submitNo);
            container.getContainerProperties().setMessageListener(new KafkaConsumerListener(consumer));
            container.getContainerProperties().setMissingTopicsFatal(true);
            container.getContainerProperties().setGroupId("problem_service_group_1");
            container.setBeanName("problem_service_group_1");
            container.start();
            return container;
        } catch (Exception e) {
            log.error("error occur: topic 먼저 생성해보세요", e);
            throw new RuntimeException(e);
        }
    }
}