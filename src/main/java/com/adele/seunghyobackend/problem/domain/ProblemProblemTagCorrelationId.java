package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProblemProblemTagCorrelationId {
    private Long problemNo;
    private Long tagNo;
}
