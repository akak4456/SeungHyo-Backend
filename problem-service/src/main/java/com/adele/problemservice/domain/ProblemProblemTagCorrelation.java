package com.adele.problemservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_problem_tag_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
public class ProblemProblemTagCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "correlation_id")
    private Long correlationId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "tag_no")
    private ProblemTag tag;
}
