package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_program_language_correlation")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
public class ProblemProgramLanguageCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "correlation_id")
    private Long correlationId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "lang_code")
    private ProgramLanguage programLanguage;
}
