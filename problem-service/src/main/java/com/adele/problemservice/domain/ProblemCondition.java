package com.adele.problemservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "problem_condition")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "conditionNo")
@SequenceGenerator(name = "seq_problem_condition", sequenceName = "seq_problem_condition", initialValue = 1, allocationSize = 1)
public class ProblemCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_problem_condition")
    @Column(name = "condition_no")
    private Long conditionNo;

    @Column(name = "condition_time")
    private BigDecimal conditionTime;

    @Column(name = "condition_memory")
    private BigDecimal conditionMemory;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    @JsonIgnore
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "lang_code")
    private ProgramLanguage language;
}
