package com.adele.domainproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemGradeInfo {
    private Long problemNo;
    private String problemTitle;
    private Long submitCount;
}
