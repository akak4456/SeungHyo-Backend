package com.adele.problemservice.dto;

import com.adele.problemservice.CompileStatus;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaCompile implements Serializable {
    private CompileStatus compileStatus;
    private int caseNo;
    private String inputSource;
    private String outputSource;
}
