package com.adele.memberservice.service;

import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.TestConfig;
import com.adele.memberservice.common.exception.BadTokenException;
import com.adele.memberservice.domain.Member;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.repository.MemberRepository;
import com.adele.memberservice.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    private PasswordEncoder encoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        redisServer.start();
        memberService = new MemberServiceImpl(memberRepository, authenticationManagerBuilder, jwtTokenProvider, encoder);
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

    @Test
    @DisplayName("reissue가 성공하는지 확인해본다.")
    public void reissueTest() {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        LoginResponse token = jwtTokenProvider.generateToken(atc);
        refreshTokenService.saveRefreshToken("user1", token.getRefreshToken());
        Assertions.assertThat(memberService.reissue(token.getRefreshToken())).isNotNull();
    }

    @Test
    @DisplayName("redis 에 저장된 것과 refresh token 이 다를 때 reissue가 실패하는지 확인해본다.")
    public void reissueFailById() {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        LoginResponse token = jwtTokenProvider.generateToken(atc);
        refreshTokenService.saveRefreshToken("user1", jwtTokenProvider.generateToken(atc).getRefreshToken() + "A");
        Assertions.assertThatCode(() -> memberService.reissue(token.getRefreshToken())).isInstanceOf(BadTokenException.class);
    }

    @Test
    @DisplayName("refresh token 이 유효하지 않을 때 reissue 가 실패하는지 확인해본다.")
    public void reissueFailByNotValidRefresh() {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        LoginResponse token = jwtTokenProvider.generateToken(atc);
        refreshTokenService.saveRefreshToken("user1", token.getRefreshToken());
        Assertions.assertThatCode(() -> memberService.reissue(token.getRefreshToken() + "ABC")).isInstanceOf(BadTokenException.class);
    }

    @Test
    @DisplayName("info edit 조회")
    public void getInfoEdit() {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        GetInfoEditResponse result = memberService.getInfoEdit("user1");
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo("user1");
        assertThat(result.getStatusMessage()).isEqualTo("status1");
        assertThat(result.getEmail()).isEqualTo("email1");
    }
}
