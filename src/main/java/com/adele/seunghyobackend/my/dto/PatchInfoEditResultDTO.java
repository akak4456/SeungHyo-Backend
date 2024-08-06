package com.adele.seunghyobackend.my.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PatchInfoEditResultDTO {
    private boolean idNotMatch;
    private boolean idNotValidForm;
    private boolean statusMessageNotValidForm;
    private boolean pwNotValidForm;
    private boolean emailNotValidForm;
    private boolean pwNotMatch;
}
