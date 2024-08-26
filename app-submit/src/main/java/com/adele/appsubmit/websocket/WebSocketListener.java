package com.adele.appsubmit.websocket;

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

@Slf4j
@AllArgsConstructor
public class WebSocketListener extends AbstractConsumerSeekAware implements MessageListener<String, KafkaCompile> {

    private final WebSocketSession session;

    @Override
    public void onMessage(ConsumerRecord<String, KafkaCompile> data) {
        KafkaCompile kafkaCompile = data.value();
        log.info("compile : {}", kafkaCompile);
        if(kafkaCompile.getCompileStatus() == CompileStatus.EXIT_FOR_KAFKA) {
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(kafkaCompile);
            try {
                session.sendMessage(new TextMessage(jsonStr));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        callback.seekToBeginning(assignments.keySet());
    }


}