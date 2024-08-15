package com.adele.problemservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_pt_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
@SequenceGenerator(name = "seq_p_pt_correlation", sequenceName = "seq_p_pt_correlation", initialValue = 1, allocationSize = 1)
public class ProblemProblemTagCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_p_pt_correlation")
    @Column(name = "correlation_id")
    private Long correlationId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "tag_no")
    private ProblemTag tag;
}
