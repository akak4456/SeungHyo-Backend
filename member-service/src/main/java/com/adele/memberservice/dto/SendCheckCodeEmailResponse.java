package com.adele.memberservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SendCheckCodeEmailResponse {
    Boolean toEmailValidForm = true;
    private Long validDuration;
}
