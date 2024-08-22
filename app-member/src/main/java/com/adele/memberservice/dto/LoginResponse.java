package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
