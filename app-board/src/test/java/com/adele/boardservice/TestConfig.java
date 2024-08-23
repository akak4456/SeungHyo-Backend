package com.adele.boardservice;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.password.PasswordEncoder;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

@TestConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class TestConfig {

}
