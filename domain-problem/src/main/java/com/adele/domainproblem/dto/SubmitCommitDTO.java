package com.adele.domainproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitCommitDTO {
    /**
     * 제출한 날짜
     */
    private LocalDateTime submitDate;
    /**
     * 제출 횟수
     */
    private Long submitCount;
}
