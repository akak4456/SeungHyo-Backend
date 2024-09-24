package com.adele.domainboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReplyRequest {
    @NotBlank
    private String content;
    private String sourceContent;
    private String langCode;
    private String langName;
}
