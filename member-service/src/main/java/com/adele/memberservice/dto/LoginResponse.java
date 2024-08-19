package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LoginResponse {
    private Boolean memberIdValidForm = true;
    private Boolean memberPwValidForm = true;
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
