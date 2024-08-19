package com.adele.memberservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.TestConfig;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import redis.embedded.RedisServer;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private EmailCheckCodeService emailCheckCodeService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisServer redisServer;

    @BeforeEach
    public void setUp() {
        redisServer.start();
    }

    @AfterEach
    public void destroy() {
        redisServer.stop();
    }

    @ParameterizedTest
    @DisplayName("로그인을 테스트 해본다")
    @MethodSource("provideLogin")
    public void loginTest(LoginRequest loginDTO, LoginResponse expectedResult) throws Exception {
        String content = gson.toJson(loginDTO);
        Authentication atc = new TestingAuthenticationToken(loginDTO.getMemberId(), null, "ROLE_ADMIN");
        JwtToken token = jwtTokenProvider.generateToken(atc);
        when(memberService.login(loginDTO)).thenReturn(token);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<LoginResponse>>() {}.getType();

        ApiResult<LoginResponse> response = gson.fromJson(responseJson, apiResultType);
        if(expectedResult.getAccessToken() != null) {
            expectedResult.setAccessToken(token.getAccessToken());
        }
        if(expectedResult.getRefreshToken() != null) {
            expectedResult.setRefreshToken(token.getRefreshToken());
        }
        if(expectedResult.getGrantType() != null) {
            expectedResult.setGrantType(token.getGrantType());
        }
        assertThat(response.getData()).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideLogin() {
        return Stream.of(
                Arguments.of(
                        new LoginRequest("user1", "pass1"),
                        new LoginResponse(true, true, "", "", "")
                ),
                Arguments.of(
                        new LoginRequest("", "pass1"),
                        new LoginResponse(false, true, null, null, null)
                ),
                Arguments.of(
                        new LoginRequest("user1", ""),
                        new LoginResponse(true, false, null, null, null)
                )
        );
    }

    @ParameterizedTest
    @DisplayName("이메일 체크 코드 전송을 테스트 해본다")
    @MethodSource("provideSendEmailCheckCode")
    public void sendEmailCheckCodeTest(
            SendCheckCodeEmailRequest request,
            SendCheckCodeEmailResponse expectedResult
    ) throws Exception {
        String content = gson.toJson(request);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/send-email-check-code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<SendCheckCodeEmailResponse>>() {}.getType();

        ApiResult<SendCheckCodeEmailResponse> response = gson.fromJson(responseJson, apiResultType);
        assertThat(response.getData()).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideSendEmailCheckCode() {
        return Stream.of(
                Arguments.of(
                        new SendCheckCodeEmailRequest(""),
                        new SendCheckCodeEmailResponse(false, null)
                ),
                Arguments.of(
                        new SendCheckCodeEmailRequest("akak4456@naver.com"),
                        new SendCheckCodeEmailResponse(true, 180L)
                )
        );
    }

    @ParameterizedTest
    @DisplayName("이메일 인증을 테스트 해본다")
    @MethodSource("provideValidEmail")
    public void validEmailTest(
            ValidEmailRequest request,
            boolean isValidEmail,
            ValidEmailResponse expectedResult,
            int saveEmailCalledTime
    ) throws Exception {
        String content = gson.toJson(request);
        when(emailCheckCodeService.isCheckCodeCorrect(any(), any())).thenReturn(isValidEmail);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/valid-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<ValidEmailResponse>>() {}.getType();

        ApiResult<ValidEmailResponse> response = gson.fromJson(responseJson, apiResultType);
        assertThat(response.getData()).isEqualTo(expectedResult);
        verify(emailCheckCodeService, times(saveEmailCalledTime)).saveValidEmail(any());
    }

    private static Stream<Arguments> provideValidEmail() {
        return Stream.of(
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", "1234"),
                        true,
                        new ValidEmailResponse(true, true, true),
                        1
                ),
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", ""),
                        true,
                        new ValidEmailResponse(true, false, true),
                        0
                ),
                Arguments.of(
                        new ValidEmailRequest("", "1234"),
                        true,
                        new ValidEmailResponse(false, true, true),
                        0
                ),
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", "1234"),
                        false,
                        new ValidEmailResponse(true, true, false),
                        0
                )
        );
    }

    @ParameterizedTest
    @DisplayName("회원가입을 테스트 해본다")
    @MethodSource("provideJoin")
    public void joinTest(
            JoinRequest joinRequest,
            boolean isIdDuplicate,
            boolean isEmailDuplicate,
            boolean isValidEmail,
            JoinResponse expectedResult,
            int joinCalledCount) throws Exception {
        String content = gson.toJson(joinRequest);
        when(memberService.isIdExist(any())).thenReturn(isIdDuplicate);
        when(memberService.isEmailExist(any())).thenReturn(isEmailDuplicate);
        when(emailCheckCodeService.isValidEmail(any())).thenReturn(isValidEmail);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<JoinResponse>>() {}.getType();

        ApiResult<JoinResponse> response = gson.fromJson(responseJson, apiResultType);
        assertThat(response.getData()).isEqualTo(expectedResult);
        verify(memberService, times(joinCalledCount)).join(any());
    }

    private static Stream<Arguments> provideJoin() {
        return Stream.of(
                Arguments.of(
                        new JoinRequest("", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        true,
                        new JoinResponse(false, true, true, true, true, true, true, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "","", "status1", "email1"),
                        false,
                        false,
                        true,
                        new JoinResponse(true, false, false, true, true, true, true, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "", "email1"),
                        false,
                        false,
                        true,
                        new JoinResponse(true, true, true, false, true, true, true, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", ""),
                        false,
                        false,
                        true,
                        new JoinResponse(true, true, true, true, false, true, true, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass2", "status1", "email1"),
                        false,
                        false,
                        true,
                        new JoinResponse(true, true, true, true, true, false, true, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        true,
                        false,
                        true,
                        new JoinResponse(true, true, true, true, true, true, false, true ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        true,
                        true,
                        new JoinResponse(true, true, true, true, true, true, true, false ,true),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        false,
                        new JoinResponse(true, true, true, true, true, true, true, true ,false),
                        0
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        true,
                        new JoinResponse(true, true, true, true, true, true, true, true ,true),
                        1
                )
        );
    }

    @Test
    @DisplayName("reissue 가 성공하는지 확인해본다")
    public void reissueTest() throws Exception {
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberService.reissue(any())).thenReturn(jwtTokenProvider.generateToken(atc));
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/reissue")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Refresh-Token", jwtTokenProvider.generateToken(atc).getRefreshToken())
                );

    }

    @Test
    @DisplayName("info-edit 조회가 성공하는지 확인해본다")
    public void getInfoEdit() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/member/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @ParameterizedTest
    @DisplayName("info-edit 수정 테스트")
    @MethodSource("providePatchInfoEdit")
    public void patchInfoEdit(
            PatchInfoEditRequest dto,
            boolean isPwMatch,
            PatchInfoEditResponse expectedResult,
            int patchInfoEditCalledTime) throws Exception {
        String content = gson.toJson(dto);
        when(memberService.isPwMatch(any(), any())).thenReturn(isPwMatch);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<PatchInfoEditResponse>>() {}.getType();

        ApiResult<PatchInfoEditResponse> response = gson.fromJson(responseJson, apiResultType);
        assertThat(response.getData()).isEqualTo(expectedResult);
        verify(memberService, times(patchInfoEditCalledTime)).patchInfoEdit(any());
    }
    private static Stream<Arguments> providePatchInfoEdit() {
        return Stream.of(
                Arguments.of(
                    new PatchInfoEditRequest("user1", "pass1", "status1", "email1"),
                        true,
                        new PatchInfoEditResponse(true, true, true, true, true),
                        1
                ),
                Arguments.of(
                        new PatchInfoEditRequest("", "pass1", "status1", "email1"),
                        true,
                        new PatchInfoEditResponse(false, true, true, true, true),
                        0
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "", "status1", "email1"),
                        true,
                        new PatchInfoEditResponse(true, false, true, true, true),
                        0
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "", "email1"),
                        true,
                        new PatchInfoEditResponse(true, true, false, true, true),
                        0
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "status1", ""),
                        true,
                        new PatchInfoEditResponse(true, true, true, false, true),
                        0
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "status1", ""),
                        true,
                        new PatchInfoEditResponse(true, true, true, false, true),
                        0
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "status1", "email1"),
                        false,
                        new PatchInfoEditResponse(true, true, true, true, false),
                        0
                )
        );
    }

    @ParameterizedTest
    @DisplayName("비밀번호 수정 테스트")
    @MethodSource("provideChangePw")
    public void changePw(
            ChangePwRequest dto,
            boolean isPwMatch,
            ChangePwResponse expectedResult,
            int changePwCalledTime
    ) throws Exception {
        String content = gson.toJson(dto);
        when(memberService.isPwMatch(any(), any())).thenReturn(isPwMatch);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/change-pw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResult<ChangePwResponse>>() {}.getType();

        ApiResult<ChangePwResponse> response = gson.fromJson(responseJson, apiResultType);
        assertThat(response.getData()).isEqualTo(expectedResult);
        verify(memberService, times(changePwCalledTime)).changePw(any(), any());
    }

    private static Stream<Arguments> provideChangePw() {
        return Stream.of(
                Arguments.of(
                    new ChangePwRequest("pass1", "new1", "new1"),
                        true,
                         new ChangePwResponse(true, true, true, true, true, true),
                        1
                ),
                Arguments.of(
                        new ChangePwRequest("", "new1", "new1"),
                        true,
                        new ChangePwResponse(false, true, true, true, true, true),
                        0
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "", ""),
                        true,
                        new ChangePwResponse(true, false, false, true, true, true),
                        0
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "new1", "new1"),
                        false,
                        new ChangePwResponse(true, true, true, false, true, true),
                        0
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "pass1", "pass1"),
                        true,
                        new ChangePwResponse(true, true, true, true, false, true),
                        0
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "new1", "new2"),
                        true,
                        new ChangePwResponse(true, true, true, true, true, false),
                        0
                )
        );
    }

    @Test
    @DisplayName("회원탈퇴가 성공하는지 확인해본다")
    public void withdraw() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        delete("/api/v1/member/my/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                        .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}

