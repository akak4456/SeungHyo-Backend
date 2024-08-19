package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class ChangePwResponse {
    @NotNull
    private Boolean currentPwValidForm = true;
    @NotNull
    private Boolean newPwValidForm = true;
    @NotNull
    private Boolean newPwCheckValidForm = true;
    @NotNull
    private Boolean currentPwMatch = true;
    @NotNull
    private Boolean currentPwAndNewPwNotMatch = true;
    @NotNull
    private Boolean newPwMatch = true;
}