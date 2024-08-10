package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@EqualsAndHashCode
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
