package com.adele.seunghyobackend;

import com.adele.seunghyobackend.member.auth.service.impl.EmailCheckCodeService;
import com.adele.seunghyobackend.email.service.EmailService;
import com.adele.seunghyobackend.member.auth.service.impl.RefreshTokenService;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

@TestConfiguration
@EnableWebSecurity
public class TestConfig {
    private String redisHost = "localhost";
    private int redisPort = 6380;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API 이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                );
        return http.build();
    }

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
    public RefreshTokenService refreshTokenService() {
        return new RefreshTokenService(redisTemplate(), 86400);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(
                "405212aeecd7145fcb537f236f338c99b0418dba1d1f7e0109d5f9acc267c7be", // 환경 변수에 있는 값과 다르다는 점에 유의
                60,
                86400,
                refreshTokenService()
        );
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

    @Bean
    public EmailService mockEmailService() {
        return emailMessage -> {

        };
    }

    @Bean
    public EmailCheckCodeService emailCheckCodeService() {
        return new EmailCheckCodeService(redisTemplate(), 180, 1800);
    }

}
