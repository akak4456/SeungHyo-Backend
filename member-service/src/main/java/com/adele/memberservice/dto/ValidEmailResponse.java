package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ValidEmailResponse {
    private Boolean emailValidForm = true;
    private Boolean codeValidForm = true;
    private Boolean isEmailValid = true;
}
