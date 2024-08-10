package com.adele.problemservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestConfig {
    private String redisHost = "localhost";
    private int redisPort = 6380;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(this.redisHost);
        redisConfiguration.setPort(this.redisPort);
        redisConfiguration.setDatabase(0);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }

    @Bean
    public RedisServer redisServer() {
        return RedisServer.builder()
                .redisExecProvider(RedisExecProvider.defaultProvider())
                .port(redisPort)
                .setting("maxmemory 10M").build();
    }

//    @Bean
//    public EmailService mockEmailService() {
//        return emailMessage -> {
//
//        };
//    }
//
//    @Bean
//    public EmailCheckCodeService emailCheckCodeService() {
//        return new EmailCheckCodeService(redisTemplate(), 180, 1800);
//    }

}
