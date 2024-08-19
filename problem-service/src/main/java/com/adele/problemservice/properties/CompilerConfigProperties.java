package com.adele.problemservice.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "compiler")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilerConfigProperties {
    private String java11Path;
}
