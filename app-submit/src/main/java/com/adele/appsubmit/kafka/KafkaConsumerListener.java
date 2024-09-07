package com.adele.appsubmit.kafka;

import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.dto.KafkaCompile;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
public class KafkaConsumerListener extends AbstractConsumerSeekAware implements MessageListener<String, KafkaCompile> {
    private final Consumer<KafkaCompile> consumer;
    @Override
    public void onMessage(ConsumerRecord<String, KafkaCompile> data) {
        KafkaCompile kafkaCompile = data.value();
        consumer.accept(kafkaCompile);
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        callback.seekToBeginning(assignments.keySet());
    }


}