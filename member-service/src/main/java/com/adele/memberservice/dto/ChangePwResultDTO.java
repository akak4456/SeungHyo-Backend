package com.adele.memberservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class ChangePwResultDTO {
    private boolean notExistUser;
    private boolean currentPwNotMatch;
    private boolean currentPwAndNewPwMatch;
    private boolean newPwNotMatch;
    private boolean newPwNotValidForm;
}