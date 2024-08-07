package com.adele.seunghyobackend.problem.domain;

import com.adele.seunghyobackend.data.converter.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "program_language")
@Getter
@Setter
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
}
