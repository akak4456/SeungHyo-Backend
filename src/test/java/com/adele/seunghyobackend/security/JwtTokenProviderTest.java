package com.adele.seunghyobackend.security;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringJUnitConfig(TestConfig.class)
@Tag(UNIT_TEST_TAG)
public class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("generateToken 이 제대로 동작하는지 확인해본다")
    public void testGenerateToken() {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        JwtToken jwtToken = jwtTokenProvider.generateToken(atc);
        assertThat(jwtToken.getGrantType()).isNotBlank();
        assertThat(jwtToken.getAccessToken()).isNotBlank();
        assertThat(jwtToken.getRefreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("generateToken 이 정상 작동할 때 getAuthentication 이 정상작동하는지 확인한다. 그리고 validateToken 이 정상작동하는지도 확인한다.")
    public void testGetAuthentication() {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        JwtToken jwtToken = jwtTokenProvider.generateToken(atc);
        Authentication atcFromToken = jwtTokenProvider.getAuthentication(jwtToken.getAccessToken());
        assertThat(atcFromToken.getName()).isEqualTo("user1");
        assertThat(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).isTrue();
        assertThat(jwtTokenProvider.validateToken(jwtToken.getRefreshToken())).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰을 잘 확인하는지 본다.")
    public void validateTokenWhenNotValidate() {
        JwtToken jwtToken = new JwtToken("Bearer","asdfg","asdfqw");
        assertThat(jwtTokenProvider.validateToken(jwtToken.getAccessToken())).isFalse();
        assertThat(jwtTokenProvider.validateToken(jwtToken.getRefreshToken())).isFalse();
    }
}
