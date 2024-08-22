package com.adele.problemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ProblemGradeResponse {
    private Long problemNo;
    private String problemTitle;
    private List<KafkaCompile> kafkaCompiles;
}
