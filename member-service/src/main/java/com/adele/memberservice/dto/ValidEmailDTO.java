package com.adele.memberservice.dto;

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
