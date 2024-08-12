package com.adele.problemservice.kafka.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaCompile implements Serializable {
    private String msg;
}
