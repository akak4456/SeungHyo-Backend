package com.adele.problemservice.domain;

import com.adele.problemservice.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "problem_input")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "inputNo")
@SequenceGenerator(name = "seq_problem_input", sequenceName = "seq_problem_input", initialValue = 1, allocationSize = 1)
public class ProblemInput {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_problem_input")
    @Column(name = "input_no")
    private Long inputNo;

    @Column(name="is_example")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isExample;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Lob
    @Column(name="input_source")
    private String inputSource;
}
