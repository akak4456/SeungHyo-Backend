package com.adele.problemservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_pa_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
@SequenceGenerator(name = "seq_p_pa_correlation", sequenceName = "seq_p_pa_correlation", initialValue = 1, allocationSize = 1)
public class ProblemAlgorithmCategoryCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_p_pa_correlation")
    @Column(name = "correlation_id")
    private Long correlationId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "algorithm_no")
    private AlgorithmCategory algorithmCategory;
}
