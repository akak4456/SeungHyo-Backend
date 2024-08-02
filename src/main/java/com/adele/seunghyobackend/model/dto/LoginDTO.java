package com.adele.seunghyobackend.model.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private String memberId;
    private String memberPw;
}
