package com.adele.seunghyobackend.problem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProblemListDTO {
    private Long problemNo;
    private String problemTitle;
    // 아래 3개 변수는 임의의 값을 넣은것
    // TODO 제대로 된 값 삽입하기
    private Long correctPeopleCount = 2000000L; // 맞힌 사람 수
    private Long submitCount = 10000000L;
    private BigDecimal correctRatio = BigDecimal.valueOf(25.252);
}
