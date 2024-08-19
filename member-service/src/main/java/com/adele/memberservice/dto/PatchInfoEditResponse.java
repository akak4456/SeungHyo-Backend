package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class PatchInfoEditResponse {
    @NotNull
    private Boolean memberIdValidForm = true;
    @NotNull
    private Boolean memberPwValidForm = true;
    @NotNull
    private Boolean statusMessageValidForm = true;
    @NotNull
    private Boolean emailValidForm = true;
    @NotNull
    private Boolean pwMatch = true;
}