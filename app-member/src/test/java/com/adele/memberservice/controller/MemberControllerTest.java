package com.adele.memberservice.controller;

import com.adele.domainemail.service.EmailService;
import com.adele.domainmember.dto.*;
import com.adele.domainmember.service.MemberService;
import com.adele.domainredis.dto.JwtToken;
import com.adele.domainredis.jwt.JwtTokenProvider;
import com.adele.domainredis.service.EmailCheckCodeService;
import com.adele.domainredis.service.RefreshTokenService;
import com.adele.internalcommon.exception.business.*;
import com.adele.internalcommon.request.AuthHeaderConstant;
import com.adele.internalcommon.response.ApiResponse;
import com.adele.internalcommon.response.ErrorCode;
import com.adele.internalcommon.response.ErrorResponse;
import com.adele.memberservice.TestConfig;
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
import java.util.List;
import java.util.Objects;
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

    @MockBean
    private RefreshTokenService refreshTokenService;

    @ParameterizedTest
    @DisplayName("로그인을 테스트 해본다")
    @MethodSource("provideLogin")
    public void loginTest(
            LoginRequest loginDTO,
            String expectedStatus,
            JwtToken expectedResult,
            List<String> errorFieldsName
    ) throws Exception {
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
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/auth/login"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<JwtToken>>() {}.getType();

            ApiResponse<JwtToken> response = gson.fromJson(responseJson, apiResultType);
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
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
        }
    }

    private static Stream<Arguments> provideLogin() {
        return Stream.of(
                Arguments.of(
                        new LoginRequest("user1", "pass1"),
                        "OK",
                        new JwtToken("", "", ""),
                        List.of("")
                ),
                Arguments.of(
                        new LoginRequest("", "pass1"),
                        "BAD_REQUEST",
                        new JwtToken(),
                        List.of("memberId")
                ),
                Arguments.of(
                        new LoginRequest("user1", ""),
                        "BAD_REQUEST",
                        new JwtToken(),
                        List.of("memberPw")
                )
        );
    }

    @ParameterizedTest
    @DisplayName("이메일 체크 코드 전송을 테스트 해본다")
    @MethodSource("provideSendEmailCheckCode")
    public void sendEmailCheckCodeTest(
            SendCheckCodeEmailRequest request,
            String expectedStatus,
            SendCheckCodeEmailResponse expectedResult,
            List<String> errorFieldsName
    ) throws Exception {
        String content = gson.toJson(request);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/send-email-check-code")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/auth/send-email-check-code"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {

            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<SendCheckCodeEmailResponse>>() {
            }.getType();

            ApiResponse<SendCheckCodeEmailResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData()).isEqualTo(expectedResult);
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
        }
    }

    private static Stream<Arguments> provideSendEmailCheckCode() {
        return Stream.of(
                Arguments.of(
                        new SendCheckCodeEmailRequest(""),
                        "BAD_REQUEST",
                        new SendCheckCodeEmailResponse(0L),
                        List.of("toEmail")
                ),
                Arguments.of(
                        new SendCheckCodeEmailRequest("akak4456@naver.com"),
                        "OK",
                        new SendCheckCodeEmailResponse(180L),
                        List.of()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("이메일 인증을 테스트 해본다")
    @MethodSource("provideValidEmail")
    public void validEmailTest(
            ValidEmailRequest request,
            boolean isValidEmail,
            String expectedStatus,
            List<String> errorFieldsName
    ) throws Exception {
        String content = gson.toJson(request);
        if(!isValidEmail) {
            doThrow(new EmailCheckCodeNotCorrectException(ErrorCode.EMAIL_CHECK_CODE_NOT_COORECT))
                    .when(emailCheckCodeService)
                            .testCheckCodeCorrect(any(), any());
        }
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/valid-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/auth/valid-email"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            verify(emailCheckCodeService, times(1)).saveValidEmail(any());
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
            verify(emailCheckCodeService, times(0)).saveValidEmail(any());
        }
    }

    private static Stream<Arguments> provideValidEmail() {
        return Stream.of(
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", "1234"),
                        true,
                        "OK",
                        List.of()
                ),
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", ""),
                        true,
                        "BAD_REQUEST",
                        List.of("code")
                ),
                Arguments.of(
                        new ValidEmailRequest("", "1234"),
                        true,
                        "BAD_REQUEST",
                        List.of("email")
                ),
                Arguments.of(
                        new ValidEmailRequest("", ""),
                        true,
                        "BAD_REQUEST",
                        List.of("email", "code")
                ),
                Arguments.of(
                        new ValidEmailRequest("akak4456@naver.com", "1234"),
                        false,
                        "BAD_REQUEST",
                        List.of()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("로그아웃을 테스트 해본다")
    @MethodSource("provideLogout")
    public void logoutTest(
            LogoutRequest request,
            String expectedStatus,
            List<String> errorFieldsName
    ) throws Exception {
        String content = gson.toJson(request);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/auth/logout"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            verify(refreshTokenService, times(1)).deleteRefreshToken(any());
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
            verify(refreshTokenService, times(0)).deleteRefreshToken(any());
        }
    }

    private static Stream<Arguments> provideLogout() {
        return Stream.of(
                Arguments.of(
                        new LogoutRequest("access token", "refresh token"),
                        "OK",
                        List.of()
                ),
                Arguments.of(
                        new LogoutRequest("", "refresh token"),
                        "BAD_REQUEST",
                        List.of("accessToken")
                ),
                Arguments.of(
                        new LogoutRequest("access token", ""),
                        "BAD_REQUEST",
                        List.of("refreshToken")
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
            String expectedStatus,
            List<String> errorFieldsName) throws Exception {
        String content = gson.toJson(joinRequest);
        if(!joinRequest.getMemberPw().equals(joinRequest.getMemberPwCheck())) {
            doThrow(new PwAndPwCheckDoesNotSameException(ErrorCode.PW_AND_PW_CHECK_DOES_NOT_SAME)).when(memberService).join(any());
        }
        if(isIdDuplicate) {
            doThrow(new IdDuplicateException(ErrorCode.ID_DUPLICATE)).when(memberService).join(any());
        }
        if(isEmailDuplicate) {
            doThrow(new EmailDuplicateException(ErrorCode.EMAIL_DUPLICATE)).when(memberService).join(any());
        }
        if(!isValidEmail) {
            doThrow(new EmailNotValidException(ErrorCode.EMAIL_NOT_VALID)).when(emailCheckCodeService).testValidEmail(any());
        }
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/auth/join"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            verify(memberService, times(1)).join(any());
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
        }
    }

    private static Stream<Arguments> provideJoin() {
        return Stream.of(
                Arguments.of(
                        new JoinRequest("", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of("memberId")
                ),
                Arguments.of(
                        new JoinRequest("user1", "","", "status1", "email1"),
                        false,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of("memberPw")
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "", "email1"),
                        false,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of("statusMessage")
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", ""),
                        false,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of("email")
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass2", "status1", "email1"),
                        false,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        true,
                        false,
                        true,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        true,
                        true,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        false,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new JoinRequest("user1", "pass1","pass1", "status1", "email1"),
                        false,
                        false,
                        true,
                        "OK",
                        List.of()
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
        when(memberService.getInfoEdit(any())).thenReturn(new GetInfoEditResponse("user1", "status1", "email1"));
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/member/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @ParameterizedTest
    @DisplayName("info-edit 수정 테스트")
    @MethodSource("providePatchInfoEdit")
    public void patchInfoEdit(
            PatchInfoEditRequest dto,
            boolean isPwMatch,
            String expectedStatus,
            List<String> errorFieldsName) throws Exception {
        String content = gson.toJson(dto);
        if(!isPwMatch) {
            doThrow(new CurrentPwNotMatchException(ErrorCode.CURRENT_PW_NOT_MATCH)).when(memberService)
                    .patchInfoEdit(any());
        }

        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                                .content(content)
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/my/info-edit"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            verify(memberService, times(1)).patchInfoEdit(any());
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
        }
    }
    private static Stream<Arguments> providePatchInfoEdit() {
        return Stream.of(
                Arguments.of(
                    new PatchInfoEditRequest("user1", "pass1", "status1", "email1"),
                        true,
                        "OK",
                        List.of()
                ),
                Arguments.of(
                        new PatchInfoEditRequest("", "pass1", "status1", "email1"),
                        true,
                        "BAD_REQUEST",
                        List.of("memberId")
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "", "status1", "email1"),
                        true,
                        "BAD_REQUEST",
                        List.of("memberPw")
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "", "email1"),
                        true,
                        "BAD_REQUEST",
                        List.of("statusMessage")
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "status1", ""),
                        true,
                        "BAD_REQUEST",
                        List.of("email")
                ),
                Arguments.of(
                        new PatchInfoEditRequest("user1", "pass1", "status1", "email1"),
                        false,
                        "BAD_REQUEST",
                        List.of()
                )
        );
    }

    @ParameterizedTest
    @DisplayName("비밀번호 수정 테스트")
    @MethodSource("provideChangePw")
    public void changePw(
            ChangePwRequest dto,
            boolean isPwMatch,
            String expectedStatus,
            List<String> errorFieldsName
    ) throws Exception {
        String content = gson.toJson(dto);
        if(!isPwMatch) {
            doThrow(new CurrentPwNotMatchException(ErrorCode.CURRENT_PW_NOT_MATCH)).when(memberService).changePw(any(),any());
        }
        if(dto.getCurrentPw().equals(dto.getNewPw())) {
            doThrow(new CurrentPwAndNewPwMatchException(ErrorCode.CURRENT_PW_AND_NEW_PW_MATCH_EXCEPTION)).when(memberService).changePw(any(), any());
        }
        if(!dto.getNewPw().equals(dto.getNewPwCheck())) {
            doThrow(new NewPwAndNewPwCheckDoesNotMatchException(ErrorCode.NEW_PW_AND_NEW_PW_CHECK_DOES_NOT_MATCH)).when(memberService).changePw(any(), any());
        }
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/change-pw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        MvcResult mvcResult = actions
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.path").value("/api/v1/member/my/change-pw"))
                .andReturn();

        if(Objects.equals(expectedStatus, "OK")) {
            verify(memberService, times(1)).changePw(any(), any());
        } else {
            String responseJson = mvcResult.getResponse().getContentAsString();
            Type apiResultType = new TypeToken<ApiResponse<ErrorResponse>>() {}.getType();

            ApiResponse<ErrorResponse> response = gson.fromJson(responseJson, apiResultType);
            assertThat(response.getData().getErrors().stream().map(ErrorResponse.FieldError::getField)).containsOnlyOnceElementsOf(errorFieldsName);
        }
    }

    private static Stream<Arguments> provideChangePw() {
        return Stream.of(
                Arguments.of(
                    new ChangePwRequest("pass1", "new1", "new1"),
                        true,
                        "OK",
                        List.of()
                ),
                Arguments.of(
                        new ChangePwRequest("", "new1", "new1"),
                        true,
                        "BAD_REQUEST",
                        List.of("currentPw")
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "", ""),
                        true,
                        "BAD_REQUEST",
                        List.of("newPw", "newPwCheck")
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "new1", "new1"),
                        false,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "pass1", "pass1"),
                        true,
                        "BAD_REQUEST",
                        List.of()
                ),
                Arguments.of(
                        new ChangePwRequest("pass1", "new1", "new2"),
                        true,
                        "BAD_REQUEST",
                        List.of()
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
                .andExpect(jsonPath("$.status").value("OK"));
    }
}

