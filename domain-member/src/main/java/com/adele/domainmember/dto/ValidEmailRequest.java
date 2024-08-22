package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidEmailRequest {
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String email;
    @NotBlank
    private String code;
}
