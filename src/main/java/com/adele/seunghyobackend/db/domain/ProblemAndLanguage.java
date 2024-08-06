package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_and_language")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProblemAndLanguage {
    @EmbeddedId
    private ProblemAndLanguageId id;

    @ManyToOne
    @MapsId("problemNo")
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @ManyToOne
    @MapsId("langCode")
    @JoinColumn(name = "lang_code")
    private ProgramLanguage programLanguage;
}
