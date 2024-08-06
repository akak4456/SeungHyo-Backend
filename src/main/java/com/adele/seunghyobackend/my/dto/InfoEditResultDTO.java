package com.adele.seunghyobackend.my.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class InfoEditResultDTO {
    private String memberId;
    private String statusMessage;
    private String email;
}
