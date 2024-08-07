package com.adele.seunghyobackend.problem.dto;

import com.adele.seunghyobackend.problem.domain.AlgorithmCategory;
import com.adele.seunghyobackend.problem.domain.ProblemCondition;
import com.adele.seunghyobackend.problem.domain.ProblemTag;
import com.adele.seunghyobackend.problem.domain.ProgramLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"problemTitle"})
public class ProblemOneDTO {
    private String problemTitle;
    private List<ProblemTag> problemTags;
    private ProblemCondition problemCondition;
    private BigDecimal correctRatio;
    private String problemExplain;
    private String problemInputExplain;
    private String problemOutputExplain;
    private String problemInput;
    private String problemOutput;
    private AlgorithmCategory algorithmCategory;
    private List<ProgramLanguage> programLanguages;
}
