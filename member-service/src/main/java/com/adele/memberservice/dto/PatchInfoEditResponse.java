package com.adele.memberservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@EqualsAndHashCode
public class PatchInfoEditResponse {
    private Boolean memberIdValidForm = true;
    private Boolean memberPwValidForm = true;
    private Boolean statusMessageValidForm = true;
    private Boolean emailValidForm = true;
    private Boolean pwMatch = true;
}