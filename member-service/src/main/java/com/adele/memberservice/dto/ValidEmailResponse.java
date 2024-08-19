package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ValidEmailResponse {
    @NotNull
    private Boolean emailValidForm = true;
    @NotNull
    private Boolean codeValidForm = true;
    @NotNull
    private Boolean isEmailValid = true;
}
