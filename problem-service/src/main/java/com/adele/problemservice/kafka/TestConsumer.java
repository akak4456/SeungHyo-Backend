package com.adele.problemservice.kafka;

import com.adele.problemservice.kafka.dto.KafkaCompile;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestConsumer {

    @KafkaListener(id = "receiver-api",
            groupId = "problem_service_group_1",
            topicPartitions =
                    { @TopicPartition(topic = "topic",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))})
    public void listener(KafkaCompile data){
        log.info(data.toString());
    }
}
