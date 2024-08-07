package com.adele.seunghyobackend.member.auth.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogoutDTO {
    private String accessToken;
    private String refreshToken;
}
