package com.adele.problemservice.domain;

import com.adele.problemservice.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "problem_output")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "outputNo")
@SequenceGenerator(name="seq_problem_output", sequenceName = "seq_problem_output", initialValue = 1, allocationSize = 1)
public class ProblemOutput {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_problem_output")
    @Column(name = "output_no")
    private Long outputNo;

    @Column(name="is_example")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isExample;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Lob
    @Column(name="output_source")
    private String outputSource;
}
