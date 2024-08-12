package com.adele.problemservice.kafka;

import com.adele.problemservice.DotenvTestExecutionListener;
import com.adele.problemservice.dto.KafkaCompile;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Map;
import java.util.concurrent.ExecutionException;
@Slf4j
class MyListener extends AbstractConsumerSeekAware implements MessageListener<String, KafkaCompile> {

    @Override
    public void onMessage(ConsumerRecord<String, KafkaCompile> data) {
        log.info(data.toString());
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        callback.seekToBeginning(assignments.keySet());
    }
}
@SpringBootTest
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
@Slf4j
public class TestProducerTest {
    @Autowired
    private TestProducer testProducer;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, KafkaCompile> kafkaListenerContainerFactory;


    @Test
    @Disabled
    void test() throws InterruptedException, ExecutionException {
        testProducer.create();
//        ConcurrentMessageListenerContainer<String, KafkaCompile> container
//                = kafkaListenerContainerFactory
//                .createContainer("topic");
//
//        container.getContainerProperties().setMessageListener(new MyListener());
//        container.getContainerProperties().setGroupId("problem_service_group_1");
//        container.setBeanName("problem_service_group_1");
//        container.start();
//        Thread.sleep(20_000L);
    }
}
