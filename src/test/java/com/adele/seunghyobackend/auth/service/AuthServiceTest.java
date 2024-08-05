package com.adele.seunghyobackend.auth.service;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.db.domain.Member;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.db.repository.MemberRepository;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.auth.service.impl.AuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

import java.util.List;
import java.util.Optional;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class AuthServiceTest {
    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockBean
    private AuthenticationManager mockAuthenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @Autowired
    private RedisServer redisServer;

    @BeforeEach
    public void setUp() {
        redisServer.start();
        authService = new AuthServiceImpl(memberRepository, authenticationManagerBuilder, jwtTokenProvider);
    }

    @AfterEach
    public void destroy() {
        redisServer.stop();
    }

    @Test
    @DisplayName("로그인이 성공할 때")
    public void login() {
        assertThat(authService).isNotNull();
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        JwtToken token = authService.login("user1", "pass1");
        assertThat(token.getAccessToken()).isNotBlank();
        assertThat(token.getRefreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("로그인이 실패할 때")
    public void loginFail() {
        assertThat(authService).isNotNull();
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Authentication failed") {
        });
        assertThatCode(() -> authService.login("user1", "pass111"))
                .isInstanceOf(AuthenticationException.class);
    }
}
