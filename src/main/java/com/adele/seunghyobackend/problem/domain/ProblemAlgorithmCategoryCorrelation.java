package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_algorithm_category_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProblemAlgorithmCategoryCorrelation {
    @EmbeddedId
    private ProblemAlgorithmCategoryCorrelationId id;

    @ManyToOne
    @MapsId("problemNo")
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @MapsId("algorithmCode")
    @JoinColumn(name = "algorithm_code")
    private AlgorithmCategory algorithmCategory;
}
