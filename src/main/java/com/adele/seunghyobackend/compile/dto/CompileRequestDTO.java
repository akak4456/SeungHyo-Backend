package com.adele.seunghyobackend.compile.dto;

import lombok.Getter;

@Getter
public class CompileRequestDTO {
    private String langCode;
    private Long problemNo;
    private String sourceCode;
}
