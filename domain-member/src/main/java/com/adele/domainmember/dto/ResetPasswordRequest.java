package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @Pattern(regexp = FormPattern.ID_PATTERN)
    private String id;
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String email;
}
