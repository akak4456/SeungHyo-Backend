package com.adele.problemservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NewSubmitRequestDTO {
    private Long problemNo;
    private String langCode;
    private String sourceCodeDisclosureScope;
    private String sourceCode;
}
