package com.adele.seunghyobackend.db.domain;

import com.adele.seunghyobackend.db.converter.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "problem")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "problemNo")
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_no")
    private Long problemNo;

    @Column(name = "problem_title")
    private String problemTitle;

    @Column(name = "problem_explain")
    private String problemExplain;

    @Column(name = "problem_input_explain")
    private String problemInputExplain;

    @Column(name = "problem_output_explain")
    private String problemOutputExplain;

    @Column(name="is_gradable")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isGradable;
}
