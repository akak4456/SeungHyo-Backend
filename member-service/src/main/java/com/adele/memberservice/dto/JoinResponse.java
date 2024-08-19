package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class JoinResponse {
    @NotNull
    private Boolean memberIdValidForm = true;
    @NotNull
    private Boolean memberPwValidForm = true;
    @NotNull
    private Boolean memberPwCheckValidForm = true;
    @NotNull
    private Boolean statusMessageValidForm = true;
    @NotNull
    private Boolean emailValidForm = true;
    @NotNull
    private Boolean pwAndPwCheckSame = true;
    @NotNull
    private Boolean idNotDuplicate = true;
    @NotNull
    private Boolean emailNotDuplicate = true;
    @NotNull
    private Boolean emailValidate = true;
}
