package com.adele.domainmember.service;

import com.adele.domainmember.TestConfig;
import com.adele.domainmember.domain.Member;
import com.adele.domainmember.dto.GetInfoEditResponse;
import com.adele.domainmember.dto.LoginRequest;
import com.adele.domainmember.dto.LoginResponse;
import com.adele.domainmember.jwt.JwtTokenProvider;
import com.adele.domainmember.repository.MemberRepository;
import com.adele.domainmember.service.impl.MemberServiceImpl;
import com.adele.internalcommon.exception.BadTokenException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private PasswordEncoder encoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        memberService = new MemberServiceImpl(memberRepository, authenticationManagerBuilder, jwtTokenProvider, encoder);
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
