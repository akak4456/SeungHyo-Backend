package com.adele.domainproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitStatisticsResponse {
    /**
     * 올해 1년 동안 맞은 제출 비율
     */
    private List<SubmitInYear> ratioInCurrentYear;

    /**
     * 여태까지 활동하면서 맞은 갯수
     */
    private Long rightCount;

    /**
     * 여태까지 활동하면서 틀린 갯수
     */
    private Long wrongCount;

    /**
     * 작년과 올해 제출한 횟수
     */
    private List<SubmitCommitDTO> commits;

    /**
     * 맞은 문제 번호
     */
    private List<Long> rightProblemNo;

    /**
     * 틀린 문제 번호
     */
    private List<Long> wrongProblemNo;

}
