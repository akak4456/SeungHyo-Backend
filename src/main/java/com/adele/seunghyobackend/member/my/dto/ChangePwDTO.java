package com.adele.seunghyobackend.member.my.dto;

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
