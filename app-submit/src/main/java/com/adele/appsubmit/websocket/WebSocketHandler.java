package com.adele.appsubmit.websocket;

import com.adele.appsubmit.kafka.KafkaDynamicListener;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.dto.KafkaCompile;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final KafkaDynamicListener kafkaDynamicListener;
    /**
     * first: submitNo
     * second.first: session id
     * second.second: websocket
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> sessions = new ConcurrentHashMap<>();
    /**
     * first: submit No
     * second: kafka consumer
     */
    private final ConcurrentHashMap<String, ConcurrentMessageListenerContainer<String, KafkaCompile>> kafkaConsumers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String submitNo = getSubmitNoFromSession(session);
        ConcurrentHashMap<String, WebSocketSession> m = sessions.computeIfAbsent(submitNo, k -> new ConcurrentHashMap<>());
        m.put(session.getId(), session);
        log.info("session established {} submitNo {}", session.getId(), submitNo);
        if(!kafkaConsumers.containsKey(submitNo)) {
            kafkaConsumers.put(submitNo, kafkaDynamicListener.addDynamicTopicListener(submitNo, (kafkaCompile) -> {
                log.info("kafka compile {}", kafkaCompile);
                if (kafkaCompile.getCompileStatus() == CompileStatus.EXIT_FOR_KAFKA) {
                    ConcurrentMessageListenerContainer<String, KafkaCompile> kafkaConsumer = kafkaConsumers.get(submitNo);
                    kafkaConsumer.stop();
                    kafkaConsumer.destroy();
                    kafkaConsumers.remove(submitNo);
                    for (Map.Entry<String, WebSocketSession> entry : sessions.get(submitNo).entrySet()) {
                        try {
                            entry.getValue().close();
                        } catch (IOException e) {
                            // no-op
                            log.error("error occur", e);
                        }
                    }
                    sessions.remove(submitNo);
                    log.info("submit {} close done", submitNo);
                } else {
                    for (Map.Entry<String, WebSocketSession> entry : sessions.get(submitNo).entrySet()) {
                        try {
                            WebSocketSession target = entry.getValue();
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(kafkaCompile);
                            target.sendMessage(new TextMessage(jsonStr));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        // no-op
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String submitNo = getSubmitNoFromSession(session);
        if(sessions.containsKey(submitNo)) {
            ConcurrentHashMap<String, WebSocketSession> m = sessions.get(submitNo);
            m.remove(session.getId());
            log.info("session closed {}", session.getId());
        } else {
            log.info("session not closed because already removed or else exception {}", session.getId());
        }
    }

    private String getSubmitNoFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        assert uri != null;
        String path = uri.getPath();
        String[] pathParts = path.split("/");
        String submitNo = pathParts.length > 2 ? pathParts[2] : null;
        log.info("session submitNo {}", submitNo);
        return submitNo;
    }
}
