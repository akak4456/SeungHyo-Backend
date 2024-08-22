package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
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
