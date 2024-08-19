package com.adele.memberservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class ChangePwResponse {
    private Boolean currentPwValidForm = true;
    private Boolean newPwValidForm = true;
    private Boolean newPwCheckValidForm = true;
    private Boolean currentPwMatch = true;
    private Boolean currentPwAndNewPwNotMatch = true;
    private Boolean newPwMatch = true;
}