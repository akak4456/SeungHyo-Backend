package com.adele.domainredis.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfigProperties {
    private String secret;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
