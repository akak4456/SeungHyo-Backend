package com.adele.problemservice.dto;

import com.adele.problemservice.CompileErrorReason;
import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.RuntimeErrorReason;
import com.adele.problemservice.domain.ProblemInput;
import com.adele.problemservice.domain.ProblemOutput;
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
