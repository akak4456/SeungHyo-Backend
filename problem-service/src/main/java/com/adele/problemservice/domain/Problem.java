package com.adele.problemservice.domain;

import com.adele.problemservice.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "problem")
@Getter
@ToString(exclude = "submitList")
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

    @OneToMany(mappedBy = "problem")
    private List<SubmitList> submitList;

    @OneToMany(mappedBy = "problem")
    private List<ProblemProblemTagCorrelation> tagCorrelations;

    @OneToMany(mappedBy = "problem")
    private List<ProblemAlgorithmCategoryCorrelation> algorithmCategoryCorrelations;

    @OneToMany(mappedBy = "problem")
    private List<ProblemProgramLanguageCorrelation> programLanguageCorrelations;

    @OneToMany(mappedBy = "problem")
    private List<ProblemInput> problemInputs;

    @OneToMany(mappedBy = "problem")
    private List<ProblemOutput> problemOutputs;

    @OneToMany(mappedBy = "problem")
    private List<ProblemCondition> problemConditions;
}
