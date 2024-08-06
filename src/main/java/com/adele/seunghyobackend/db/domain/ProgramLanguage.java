package com.adele.seunghyobackend.db.domain;

import com.adele.seunghyobackend.db.converter.BooleanToYNConverter;
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
