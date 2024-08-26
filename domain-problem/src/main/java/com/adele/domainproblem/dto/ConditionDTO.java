package com.adele.domainproblem.dto;

import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDTO {
    private List<ProblemInput> input;
    private List<ProblemOutput> output;
    private BigDecimal timeCondition;
    private BigDecimal memoryCondition;
}
