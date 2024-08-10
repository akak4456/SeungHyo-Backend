package com.adele.problemservice.dto;

import com.adele.problemservice.domain.AlgorithmCategory;
import com.adele.problemservice.domain.ProblemCondition;
import com.adele.problemservice.domain.ProblemTag;
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
    private List<ProblemCondition> problemCondition;
    private BigDecimal correctRatio;
    private String problemExplain;
    private String problemInputExplain;
    private String problemOutputExplain;
    private List<String> problemInput;
    private List<String> problemOutput;
    private List<AlgorithmCategory> algorithmCategory;
}
