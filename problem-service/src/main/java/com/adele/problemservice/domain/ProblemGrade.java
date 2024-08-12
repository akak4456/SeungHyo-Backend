package com.adele.problemservice.domain;

import com.adele.problemservice.CompileStatus;
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
}
