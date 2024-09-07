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
     * sessions.first: submitNo
     * sessions.second.first: session id
     * sessions.second.second: target websocket
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> sessions =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<KafkaCompile>> submitNoToCompileResults = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentMessageListenerContainer<String, KafkaCompile>> kafkaConsumers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String submitNo = getSubmitNoFromSession(session);
        ConcurrentHashMap<String, WebSocketSession> m = sessions.computeIfAbsent(submitNo, k -> new ConcurrentHashMap<>());
        m.put(session.getId(), session);
        log.info("session established {}", session.getId());
        if(!submitNoToCompileResults.containsKey(submitNo)) {
            kafkaConsumers.put(submitNo, kafkaDynamicListener.addDynamicTopicListener(submitNo, (kafkaCompile) -> {
                if(kafkaCompile.getCompileStatus() == CompileStatus.EXIT_FOR_KAFKA) {
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
                    submitNoToCompileResults.remove(submitNo);
                    log.info("submit {} close done", submitNo);
                } else {
                    CopyOnWriteArrayList<KafkaCompile> compileResults = submitNoToCompileResults.computeIfAbsent(submitNo, k -> new CopyOnWriteArrayList<>());
                    compileResults.add(kafkaCompile);
                    for (Map.Entry<String, WebSocketSession> entry : sessions.get(submitNo).entrySet()) {
                        try {
                            WebSocketSession target = entry.getValue();
                            target.sendMessage(new TextMessage("!total " + compileResults.size()));
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
        try {
            StringTokenizer st = new StringTokenizer(message.getPayload(), ":");
            int begin = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            String submitNo = getSubmitNoFromSession(session);
            if (submitNoToCompileResults.containsKey(submitNo)) {
                CopyOnWriteArrayList<KafkaCompile> compileResults = submitNoToCompileResults.get(submitNo);
                for (int i = begin; i < end; i++) {
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(compileResults.get(i));
                    session.sendMessage(new TextMessage(jsonStr));
                }
            }
        } catch (Exception e) {
            // no-op
            log.error("error occur", e);
        }
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
