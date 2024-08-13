package com.adele.problemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProblemGradeResponse {
    private List<KafkaCompile> kafkaCompiles;
}
