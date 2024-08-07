package com.adele.seunghyobackend.member.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class JoinResultDTO {
    private boolean idNotValidForm;
    private boolean idDuplicate;
    private boolean statusNotValidForm;
    private boolean pwNotValidForm;
    private boolean pwAndPwCheckDifferent;
    private boolean emailNotValidForm;
    private boolean emailDuplicate;
    private boolean emailNotValidate;
}
