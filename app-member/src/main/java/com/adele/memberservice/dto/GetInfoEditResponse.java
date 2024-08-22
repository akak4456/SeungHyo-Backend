package com.adele.memberservice.dto;

import com.adele.memberservice.FormPattern;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class GetInfoEditResponse {
    @Pattern(regexp = FormPattern.ID_PATTERN)
    private String memberId;
    @Pattern(regexp = FormPattern.STATUS_PATTERN)
    private String statusMessage;
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String email;
}
