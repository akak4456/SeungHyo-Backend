package com.adele.problemservice.kafka;

import com.adele.problemservice.DotenvTestExecutionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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

    @Test
    @Disabled
    void test() throws InterruptedException, ExecutionException {
        testProducer.create();
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        try (AdminClient client = AdminClient.create(props)) {
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true); // includes internal topics such as __consumer_offsets
            ListTopicsResult topics = client.listTopics(options);
            Set<String> currentTopicList = topics.names().get();
            // do your filter logic here......
            for(String topic : currentTopicList) {
                log.info("current topic: {}", topic);
            }
        }
        Thread.sleep(20_000L);
    }
}
