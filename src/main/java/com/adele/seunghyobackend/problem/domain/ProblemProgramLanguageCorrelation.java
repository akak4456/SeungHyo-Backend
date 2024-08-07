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
@EqualsAndHashCode(of = "id")
public class ProblemProgramLanguageCorrelation {
    @EmbeddedId
    private ProblemProgramLanguageCorrelationId id;

    @ManyToOne
    @MapsId("problemNo")
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @MapsId("langCode")
    @JoinColumn(name = "lang_code")
    private ProgramLanguage programLanguage;
}
