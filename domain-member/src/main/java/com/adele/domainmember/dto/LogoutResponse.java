package com.adele.domainmember.dto;

import jakarta.validation.constraints.NotNull;

public class LogoutResponse {
    @NotNull
    private Boolean accessTokenValidForm = true;
    @NotNull
    private Boolean refreshTokenValidForm = true;
}
