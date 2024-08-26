package com.adele.domainproblem.dto;

import com.adele.domainproblem.CompileErrorReason;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.RuntimeErrorReason;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaCompile implements Serializable {
    private CompileStatus compileStatus;
    private Long caseNo;
    private String inputSource;
    private String outputSource;
    private CompileErrorReason compileErrorReason;
    private RuntimeErrorReason runtimeErrorReason;
}
