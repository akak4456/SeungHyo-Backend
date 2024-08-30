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
public class ProblemInfoInMainPage {
    private Long allProblemCount;
    private Long availableProblemCount;
    private Long correctProblemCount;
    private Long availableLanguageCount;
    private List<ProblemGradeInfo> problemGradeInfoList;
}
