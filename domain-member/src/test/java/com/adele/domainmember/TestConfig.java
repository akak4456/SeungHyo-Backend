package com.adele.domainmember;

import com.adele.domainmember.jwt.JwtConfigProperties;
import com.adele.domainmember.jwt.JwtTokenProvider;
import com.adele.domainmember.service.RefreshTokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {
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
}
