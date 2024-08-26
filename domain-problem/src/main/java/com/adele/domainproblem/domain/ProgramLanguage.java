package com.adele.domainproblem.domain;

import com.adele.domainproblem.BooleanToYNConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "program_language")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "langCode")
public class ProgramLanguage {
    @Id
    @Column(name = "lang_code")
    private String langCode;

    @Column(name="lang_name")
    private String langName;

    @Column(name="is_gradable")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isGradable;

    @JsonIgnore
    @OneToMany(mappedBy = "programLanguage")
    private List<ProblemProgramLanguageCorrelation> correlationList;
}
