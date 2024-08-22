package com.adele.memberservice.dto;

import com.adele.memberservice.FormPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LoginRequest {
    @Pattern(regexp = FormPattern.ID_PATTERN)
    private String memberId;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String memberPw;
}
