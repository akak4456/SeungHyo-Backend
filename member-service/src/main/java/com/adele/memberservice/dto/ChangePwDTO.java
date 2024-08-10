package com.adele.memberservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class ChangePwDTO {
    private String currentPw;
    private String newPw;
    private String newPwCheck;
}
