package com.adele.problemservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;

import java.util.Properties;
import java.util.Set;

@Slf4j
public class KafkaTopicValidator {
    private final AdminClient adminClient;

    public KafkaTopicValidator(String bootstrapServers) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(properties);
    }

    public boolean isTopicValid(String topicName) {
        try {
            ListTopicsResult topics = adminClient.listTopics();
            KafkaFuture<Set<String>> names = topics.names();
            Set<String> existingTopics = names.get();
            return existingTopics.contains(topicName);
        } catch (Exception e) {
            log.error("Error checking topic validity", e);
            return false;
        }
    }

    public void close() {
        adminClient.close();
    }
}
