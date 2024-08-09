package com.adele.memberservice.service;

import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.TestConfig;
import com.adele.memberservice.domain.Member;
import com.adele.memberservice.dto.LoginRequest;
import com.adele.memberservice.dto.LoginResponse;
import com.adele.memberservice.repository.MemberRepository;
import com.adele.memberservice.service.impl.MemberServiceImpl;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import redis.embedded.RedisServer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TestConfig.class)
@Slf4j
public class MemberServiceTest {
    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @MockBean
    private AuthenticationManager mockAuthenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private MemberService memberService;

    @Autowired
    private RedisServer redisServer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        redisServer.start();
        memberService = new MemberServiceImpl(memberRepository, authenticationManagerBuilder, jwtTokenProvider, passwordEncoder);
    }

    @AfterEach
    public void destroy() {
        redisServer.stop();
    }

    @Test
    @DisplayName("로그인이 성공할 때")
    public void login() {
        assertThat(memberService).isNotNull();
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        LoginResponse token = memberService.login(new LoginRequest("user1", "pass1"));
        assertThat(token.getAccessToken()).isNotBlank();
        assertThat(token.getRefreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("로그인이 실패할 때")
    public void loginFail() {
        assertThat(memberService).isNotNull();
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Authentication failed") {
        });
        assertThatCode(() -> memberService.login(new LoginRequest("user1", "pass111")))
                .isInstanceOf(AuthenticationException.class);
    }
}
