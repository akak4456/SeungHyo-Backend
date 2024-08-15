package com.adele.problemservice.domain;

import com.adele.problemservice.CompileErrorReason;
import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.RuntimeErrorReason;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_grade")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "gradeNo")
@SequenceGenerator(name = "seq_problem_grade", sequenceName = "seq_problem_grade", initialValue = 1, allocationSize = 1)
public class ProblemGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_problem_grade")
    @Column(name = "grade_no")
    private Long gradeNo;

    @Enumerated(EnumType.STRING)
    @Column(name="grade_result")
    private CompileStatus gradeResult;

    @ManyToOne
    @JoinColumn(name = "input_no")
    private ProblemInput input;

    @ManyToOne
    @JoinColumn(name="output_no")
    private ProblemOutput output;

    @ManyToOne
    @JoinColumn(name="submit_no")
    private SubmitList submit;

    @Column(name="grade_case_no")
    private Long gradeCaseNo;

    @Column(name="compile_error_reason")
    @Enumerated(EnumType.STRING)
    private CompileErrorReason compileErrorReason;

    @Column(name="runtime_error_reason")
    @Enumerated(EnumType.STRING)
    private RuntimeErrorReason runtimeErrorReason;
}
