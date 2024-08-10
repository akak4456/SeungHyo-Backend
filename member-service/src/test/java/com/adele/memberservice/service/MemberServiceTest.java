package com.adele.memberservice.service;

import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.TestConfig;
import com.adele.memberservice.domain.Member;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.repository.MemberRepository;
import com.adele.memberservice.service.impl.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hibernate.mapping.Join;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    @ParameterizedTest
    @DisplayName("try join 테스트")
    @MethodSource("provideJoin")
    public void tryJoin(JoinDTO joinDTO, Member memberById, Member memberByEmail, boolean isEmailValid, JoinResultDTO expected, int saveCalledTime) {
        when(memberRepository.findById(anyString())).thenReturn(Optional.ofNullable(memberById));
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(memberByEmail));
        JoinResultDTO result = memberService.tryJoin(joinDTO, isEmailValid);
        Assertions.assertThat(result).isEqualTo(expected);
        verify(memberRepository, times(saveCalledTime)).save(any());
    }

    private static Stream<Arguments> provideJoin() {
        return Stream.of(
                Arguments.of(
                        new JoinDTO("", "pass1", "pass1", "status1", "email1"),
                        null, null, true,
                        new JoinResultDTO(true, false, false, false, false, false, false, false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "", "", "status1", "email1"),
                        null, null, true,
                        new JoinResultDTO(false,false,false,true,false,false,false,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "status1", ""),
                        null,null,true,
                        new JoinResultDTO(false,false,false,false,false,true,false,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "status1", "email1"),
                        new Member(),null,true,
                        new JoinResultDTO(false,true,false,false,false,false,false,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "", "email1"),
                        null,null,true,
                        new JoinResultDTO(false,false,true,false,false,false,false,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass21", "status1", "email1"),
                        null,null,true,
                        new JoinResultDTO(false,false,false,false,true,false,false,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "status1", "email1"),
                        null, new Member(), true,
                        new JoinResultDTO(false,false,false,false,false,false, true,false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "status1", "email1"),
                        null, null, false,
                        new JoinResultDTO(false,false,false,false,false,false,false,true),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass21", "status1", "email1"),
                        null, new Member(), true,
                        new JoinResultDTO(false, false, false, false, true, false, true, false),
                        0
                ),
                Arguments.of(
                        new JoinDTO("user1", "pass1", "pass1", "status1", "email1"),
                        null,null,true,
                        new JoinResultDTO(false, false, false, false,false, false, false, false),
                        1
                )
        );
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
        InfoEditResultDTO result = memberService.getInfoEdit("user1");
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo("user1");
        assertThat(result.getStatusMessage()).isEqualTo("status1");
        assertThat(result.getEmail()).isEqualTo("email1");
    }

    @ParameterizedTest
    @DisplayName("info-edit 수정 테스트")
    @MethodSource("provideInfoEdit")
    public void patchInfoEdit(
            PatchInfoEditDTO dto,
            boolean idMatch,
            PatchInfoEditResultDTO expected) {
        when(memberRepository.findById(anyString())).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        PatchInfoEditResultDTO result = memberService.patchInfoEdit(dto, idMatch);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideInfoEdit() {
        return Stream.of(
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, false, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("", "pass1", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, true, false, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, false, true, false, true)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, true, false, false, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "status1", ""), true,
                        new PatchInfoEditResultDTO(false, false, false, false, true, false)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass2", "status1", "email1"), true,
                        new PatchInfoEditResultDTO(false, false, false, false, false, true)
                ),
                Arguments.of(
                        new PatchInfoEditDTO("user1", "pass1", "status1", "email1"), false,
                        new PatchInfoEditResultDTO(true, false, false, false, false, false)
                )
        );
    }

    @ParameterizedTest
    @DisplayName("비밀번호 수정 테스트")
    @MethodSource("provideChangePw")
    public void changePw(
            String memberId,
            ChangePwDTO dto,
            ChangePwResultDTO expected
    ) {
        when(memberRepository.findById("user1")).thenReturn(Optional.of(new Member(
                "user1",
                "pass1",
                "status1",
                false,
                "email1",
                List.of("ADMIN")
        )));
        ChangePwResultDTO result = memberService.tryChangePw(memberId, dto);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideChangePw() {
        return Stream.of(
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass2","pass2"),
                        new ChangePwResultDTO(false, false, false, false, false)
                ),
                Arguments.of(
                        "user2",
                        new ChangePwDTO("pass1","pass2","pass2"),
                        new ChangePwResultDTO(true, false, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass3","pass2","pass2"),
                        new ChangePwResultDTO(false, true, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("","pass2","pass2"),
                        new ChangePwResultDTO(false, true, false, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass1","pass1"),
                        new ChangePwResultDTO(false, false, true, false, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","pass2","pass3"),
                        new ChangePwResultDTO(false, false, false, true, false)
                ),
                Arguments.of(
                        "user1",
                        new ChangePwDTO("pass1","",""),
                        new ChangePwResultDTO(false, false, false, false, true)
                )
        );
    }
}
