package com.adele.seunghyobackend.my.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PatchInfoEditDTO {
    private String memberId;
    private String memberPw;
    private String statusMessage;
    private String email;
}
