package com.adele.problemservice.dto;

import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.domain.ProblemInput;
import com.adele.problemservice.domain.ProblemOutput;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CompileResultDTO {
    private CompileStatus status;
    private ProblemInput expectedInput;
    private ProblemOutput expectedOutput;
    private String compileOutput;
    private Throwable error;
}
