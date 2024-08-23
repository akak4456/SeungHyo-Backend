package com.adele.domainboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardWriteDTO {
    @NotBlank
    private String boardTitle;
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String categoryName;
    @NotBlank
    private String langCode;
    @NotBlank
    private String langName;
    @NotBlank
    @Pattern(regexp = "\\d+")
    private String problemNo;
    @NotBlank
    private String normalHTMLContent;

    private String sourceCode;
}
