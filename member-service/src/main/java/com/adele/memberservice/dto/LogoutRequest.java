package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String accessToken;
    private String refreshToken;
}
