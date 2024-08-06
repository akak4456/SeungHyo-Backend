package com.adele.seunghyobackend.db.domain;

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
    @MapsId("tagName")
    @JoinColumn(name = "tag_name")
    private ProblemTag tag;
}
