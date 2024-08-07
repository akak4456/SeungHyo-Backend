package com.adele.seunghyobackend.problem.domain;

import com.adele.seunghyobackend.data.converter.BooleanToYNConverter;
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
public class ProblemInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "input_no")
    private Long inputNo;

    @Column(name="is_example")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isExample;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Column(name="input_file_name")
    private String inputFileName;
}
