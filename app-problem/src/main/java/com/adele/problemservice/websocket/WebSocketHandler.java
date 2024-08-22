package com.adele.problemservice.websocket;

import com.adele.problemservice.dto.KafkaCompile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ConcurrentKafkaListenerContainerFactory<String, KafkaCompile> kafkaListenerContainerFactory;
    private ConcurrentHashMap<String, WebSocketSession> sessions =
            new ConcurrentHashMap<String, WebSocketSession>();
    private ConcurrentHashMap<String, ConcurrentMessageListenerContainer<String, KafkaCompile>> kafkaConsumers
            = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.put(session.getId(), session);
        log.info("session established {}", session.getId());
        URI uri = session.getUri();
        assert uri != null;
        String path = uri.getPath();
        String[] pathParts = path.split("/");
        String submitNo = pathParts.length > 2 ? pathParts[2] : null;
        log.info("session submitNo {}", submitNo);
        try {
            ConcurrentMessageListenerContainer<String, KafkaCompile> container
                    = kafkaListenerContainerFactory
                    .createContainer("submit." + submitNo);
            container.getContainerProperties().setMessageListener(new WebSocketListener(session));
            container.getContainerProperties().setMissingTopicsFatal(true);
            container.getContainerProperties().setGroupId("problem_service_group_1");
            container.setBeanName("problem_service_group_1");
            container.start();
            kafkaConsumers.put(session.getId(), container);
        } catch (Exception e) {
            log.error("error occur: topic 먼저 생성해보세요", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session.getId());
        ConcurrentMessageListenerContainer<String, KafkaCompile> container = kafkaConsumers.get(session.getId());
        if(container != null) {
            container.stop();
            container.destroy();
            kafkaConsumers.remove(session.getId());
        }
        log.info("session closed {}", session.getId());
    }
}
