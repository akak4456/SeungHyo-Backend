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
public class ProblemOutput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "output_no")
    private Long outputNo;

    @Column(name="is_example")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isExample;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Column(name="output_source")
    private String outputSource;
}
