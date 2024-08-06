package com.adele.seunghyobackend.auth.service;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.auth.dto.JoinDTO;
import com.adele.seunghyobackend.auth.dto.JoinResultDTO;
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
import static org.mockito.Mockito.*;

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
                false,
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
                false,
                "email1",
                List.of("ADMIN")
        )));
        when(authenticationManagerBuilder.getObject()).thenReturn(mockAuthenticationManager);
        when(mockAuthenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Authentication failed") {
        });
        assertThatCode(() -> authService.login("user1", "pass111"))
                .isInstanceOf(AuthenticationException.class);
    }
    @Test
    @DisplayName("tryJoin - 아이디가 올바른 형태가 아닐때")
    public void tryJoinIdNotValidForm() {
        JoinDTO joinDTO = new JoinDTO("", "pass1", "pass1", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isTrue();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }
    @Test
    @DisplayName("tryJoin - 비밀번호가 올바른 형태가 아닐때")
    public void tryJoinPwNotValidForm() {
        JoinDTO joinDTO = new JoinDTO("user1", "", "", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isTrue();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }
    @Test
    @DisplayName("tryJoin - 이메일이 올바른 형태가 아닐때")
    public void tryJoinEmailNotValidForm() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "status1", "");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isTrue();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }
    @Test
    @DisplayName("tryJoin - 아이디 중복 될때")
    public void tryJoinIdDuplicate() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member()));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isTrue();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 상태 메시지가 올바른 형태가 아닐때")
    public void tryJoinStatusMessageEmpty() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isTrue();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 비밀번호와 비밀번호 확인이 다를때")
    public void tryJoinPwAndPwCheckIsDifferent() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass21", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isTrue();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 이메일이 중복될때")
    public void tryJoinEmailDuplicate() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(new Member()));
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isTrue();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 이메일 인증을 안했다면?")
    public void tryJoinEmailNotValidate() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, false);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isTrue();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 두가지 이상 조건이 잘못되면?")
    public void tryJoinTwoConditionWrong() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass21", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(new Member()));
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isTrue();
        assertThat(result.isEmailDuplicate()).isTrue();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("tryJoin - 성공할 때")
    public void tryJoinEmailSuccess() {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1", "pass1", "status1", "email1");
        when(memberRepository.findById(anyString())).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        JoinResultDTO result = authService.tryJoin(joinDTO, true);
        assertThat(result.isIdNotValidForm()).isFalse();
        assertThat(result.isPwNotValidForm()).isFalse();
        assertThat(result.isEmailNotValidForm()).isFalse();
        assertThat(result.isIdDuplicate()).isFalse();
        assertThat(result.isStatusNotValidForm()).isFalse();
        assertThat(result.isPwAndPwCheckDifferent()).isFalse();
        assertThat(result.isEmailDuplicate()).isFalse();
        assertThat(result.isEmailNotValidate()).isFalse();
        verify(memberRepository, times(1)).save(any());
    }
}
