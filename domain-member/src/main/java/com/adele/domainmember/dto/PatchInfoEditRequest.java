package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PatchInfoEditRequest {
    @Pattern(regexp = FormPattern.ID_PATTERN)
    private String memberId;
    @Pattern(regexp = FormPattern.PW_PATTERN)
    private String memberPw;
    @Pattern(regexp = FormPattern.STATUS_PATTERN)
    private String statusMessage;
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String email;
}
