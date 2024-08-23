package com.adele.domainredis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfigProperties {
    private Long emailCheckCodeValidInSeconds = 180L;
    private Long validEmailTimeInSeconds = 180L;
}
