package com.adele.problemservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "problemTitle")
public class ProblemListDTO {
    private Long problemNo;
    private String problemTitle;
    private Long correctPeopleCount; // 맞힌 사람 수
    private Long submitCount;
    private BigDecimal correctRatio;
}
