package com.adele.domainmember.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class LoginResponse {
    @NotBlank
    private String grantType;
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
