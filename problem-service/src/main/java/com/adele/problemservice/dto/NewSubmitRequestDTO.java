package com.adele.problemservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewSubmitRequestDTO {
    private Long problemNo;
    private String langCode;
    private String sourceCodeDisclosureScope;
    private String sourceCode;
}
