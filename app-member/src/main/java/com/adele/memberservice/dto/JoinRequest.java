package com.adele.memberservice.dto;

import com.adele.memberservice.FormPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinRequest {
    @Pattern(regexp = FormPattern.ID_PATTERN)
    private String memberId;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String memberPw;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String memberPwCheck;
    @Pattern(regexp = FormPattern.STATUS_PATTERN)
    private String statusMessage;
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String email;
}
