package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_problem_tag_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProblemProblemTagCorrelation {
    @EmbeddedId
    private ProblemProblemTagCorrelationId id;

    @ManyToOne
    @MapsId("problemNo")
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @MapsId("tagNo")
    @JoinColumn(name = "tag_no")
    private ProblemTag tag;
}
