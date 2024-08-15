package com.adele.problemservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_pl_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
@SequenceGenerator(name="seq_p_pl_correlation", sequenceName = "seq_p_pl_correlation", initialValue = 1, allocationSize = 1)
public class ProblemProgramLanguageCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_p_pl_correlation")
    @Column(name = "correlation_id")
    private Long correlationId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "lang_code")
    private ProgramLanguage programLanguage;
}
