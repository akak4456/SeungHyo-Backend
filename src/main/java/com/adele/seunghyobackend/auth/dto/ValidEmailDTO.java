package com.adele.seunghyobackend.auth.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidEmailDTO {
    private String email;
    private String code;
}
