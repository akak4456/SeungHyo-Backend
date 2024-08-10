package com.adele.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinDTO {
    private String memberId;
    private String memberPw;
    private String memberPwCheck;
    private String statusMessage;
    private String email;
}
