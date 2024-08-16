package com.adele.boardservice.dto;

import com.adele.common.FormErrorCode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardWriteDTO {
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String boardTitle;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String categoryCode;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String categoryName;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String langCode;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String langName;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    @Pattern(regexp = "\\d+", message = FormErrorCode.ONLY_NUMBER)
    private String problemNo;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String normalHTMLContent;
    @NotBlank(message = FormErrorCode.NOT_BLANK)
    private String sourceCode;
}
