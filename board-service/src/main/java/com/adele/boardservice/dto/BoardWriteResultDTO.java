package com.adele.boardservice.dto;

import com.adele.common.FormErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BoardWriteResultDTO {
    private String boardTitleError = "";
    private String categoryCodeError = "";
    private String categoryNameError = "";
    private String langCodeError = "";
    private String langNameError = "";
    private String problemNoError = "";
    private String normalHTMLContentError = "";
    private String sourceCodeError = "";
    private Boolean isProblemNoValid = true;
}
