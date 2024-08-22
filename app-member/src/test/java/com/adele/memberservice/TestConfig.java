package com.adele.memberservice;

import com.adele.domainemail.EmailConfigProperties;
import com.adele.domainmember.jwt.JwtConfigProperties;
import com.adele.domainmember.jwt.JwtTokenProvider;
import com.adele.domainmember.service.RefreshTokenService;
import com.adele.domainmember.service.impl.RefreshTokenServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
@EnableConfigurationProperties({JwtConfigProperties.class, EmailConfigProperties.class})
@EnableAspectJAutoProxy(exposeProxy = true)
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
    public RefreshTokenService refreshTokenService() {
        return new RefreshTokenService() {
            @Override
            public void saveRefreshToken(String memberId, String refreshToken) {

            }

            @Override
            public void validateRefreshToken(String memberId, String refreshToken) {

            }

            @Override
            public void deleteRefreshToken(String memberId) {

            }
        };
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(new JwtConfigProperties(
                "405212aeecd7145fcb537f236f338c99b0418dba1d1f7e0109d5f9acc267c7be", // 환경 변수에 있는 값과 다르다는 점에 유의
                60L,
                86400L),
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

}
