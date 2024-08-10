package com.adele.problemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConditionDTO {
    private List<String> input;
    private List<String> output;
    private BigDecimal timeCondition;
    private BigDecimal memoryCondition;
}
