package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class ChangePwRequest {
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String currentPw;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String newPw;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String newPwCheck;
}
