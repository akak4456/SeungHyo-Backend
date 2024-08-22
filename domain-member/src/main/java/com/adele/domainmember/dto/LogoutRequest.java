package com.adele.domainmember.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
