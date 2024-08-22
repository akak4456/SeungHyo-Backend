package com.adele.problemservice.dto;

import com.adele.problemservice.CompileErrorReason;
import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.RuntimeErrorReason;
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
