package com.adele.appsubmit;

import com.adele.appsubmit.properties.CompilerConfigProperties;
import com.adele.appsubmit.properties.KafkaConfigProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@EnableConfigurationProperties({KafkaConfigProperties.class, CompilerConfigProperties.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class TestConfig {

}
