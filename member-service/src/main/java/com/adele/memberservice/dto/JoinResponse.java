package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class JoinResponse {
    private Boolean memberIdValidForm = true;
    private Boolean memberPwValidForm = true;
    private Boolean memberPwCheckValidForm = true;
    private Boolean statusMessageValidForm = true;
    private Boolean emailValidForm = true;
    private Boolean pwAndPwCheckSame = true;
    private Boolean idNotDuplicate = true;
    private Boolean emailNotDuplicate = true;
    private Boolean emailValidate = true;
}
