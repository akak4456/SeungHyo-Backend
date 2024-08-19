package com.adele.memberservice.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class GetInfoEditResponse {
    private String memberId;
    private String statusMessage;
    private String email;
}
