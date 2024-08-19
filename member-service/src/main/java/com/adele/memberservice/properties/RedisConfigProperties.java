package com.adele.memberservice.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisConfigProperties {
    private String host;
    private String port;
}
