package com.adele.memberservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SendCheckCodeEmailResponse {
    @NotNull
    Boolean toEmailValidForm = true;
    @NotNull
    private Long validDuration = 0L;
}
