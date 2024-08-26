package com.adele.domainproblem.dto;

import com.adele.domainproblem.CompileErrorReason;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.RuntimeErrorReason;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"expectedInput", "expectedOutput"})
@Setter
public class CompileResultDTO {
    private CompileStatus status;
    private ProblemInput expectedInput;
    private ProblemOutput expectedOutput;
    private String compileOutput;
    private Throwable error;
    private CompileErrorReason compileErrorReason;
    private RuntimeErrorReason runtimeErrorReason;
}
