package com.adele.domainproblem.domain;

import com.adele.domainproblem.CompileErrorReason;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.RuntimeErrorReason;
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
public class ProblemGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
