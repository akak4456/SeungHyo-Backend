package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LoginResponse {
    @NotNull
    private Boolean memberIdValidForm = true;
    @NotNull
    private Boolean memberPwValidForm = true;
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
