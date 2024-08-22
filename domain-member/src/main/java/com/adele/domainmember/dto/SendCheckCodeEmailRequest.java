package com.adele.domainmember.dto;

import com.adele.domainmember.FormPattern;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SendCheckCodeEmailRequest {
    @Pattern(regexp = FormPattern.EMAIL_PATTERN)
    private String toEmail;
}
