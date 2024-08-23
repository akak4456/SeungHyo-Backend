package com.adele.domainredis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class JwtToken {
    @NotBlank
    private String grantType;
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
