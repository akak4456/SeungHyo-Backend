package com.adele.domainproblem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NewSubmitRequestDTO {
    @NotNull
    private Long problemNo;
    @NotBlank
    private String langCode;
    @NotBlank
    private String sourceCodeDisclosureScope;
    @NotBlank
    private String sourceCode;
}
