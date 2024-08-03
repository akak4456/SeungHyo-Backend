package com.adele.seunghyobackend.member.service;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.member.model.domain.Member;
import com.adele.seunghyobackend.security.model.dto.JwtToken;
import com.adele.seunghyobackend.member.repository.MemberRepository;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.member.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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

    @BeforeEach
    public void setUp() {
        memberService = new MemberServiceImpl(memberRepository, authenticationManagerBuilder, jwtTokenProvider);
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
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(atc);
        JwtToken token = memberService.login("user1", "pass1");
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
                "Y",
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Authentication failed") {
        });
        assertThatCode(() -> memberService.login("user1", "pass111"))
                .isInstanceOf(AuthenticationException.class);
    }
}
