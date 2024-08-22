package com.adele.apigatewayservice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
