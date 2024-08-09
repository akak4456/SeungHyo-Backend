package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
