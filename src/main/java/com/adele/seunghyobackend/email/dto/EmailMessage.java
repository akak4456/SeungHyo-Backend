package com.adele.seunghyobackend.email.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmailMessage {
    private String to; // 수신자
    private String subject; // 메일 제목
    private String message; // 메일 내용
}
