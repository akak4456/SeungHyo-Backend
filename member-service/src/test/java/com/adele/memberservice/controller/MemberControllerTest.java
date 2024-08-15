package com.adele.memberservice.controller;

import com.adele.common.AuthHeaderConstant;
import com.adele.memberservice.JwtTokenProvider;
import com.adele.memberservice.TestConfig;
import com.adele.memberservice.dto.*;
import com.adele.memberservice.service.EmailCheckCodeService;
import com.adele.memberservice.service.EmailService;
import com.adele.memberservice.service.MemberService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import redis.embedded.RedisServer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @Test
    @DisplayName("로그인이 성공하는지 확인해본다")
    public void loginTest() throws Exception {
        LoginRequest loginDTO = new LoginRequest("user1", "pass1");
        String content = gson.toJson(loginDTO);
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(memberService.login(loginDTO)).thenReturn(jwtTokenProvider.generateToken(atc));
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("회원가입이 되는지 확인해본다.")
    public void joinTest() throws Exception {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1","pass1", "status1", "email1");
        String content = gson.toJson(joinDTO);

        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/member/auth/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
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

    @Test
    @DisplayName("info-edit 수정이 성공하는지 확인해본다")
    public void patchInfoEdit() throws Exception {
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "status1", "email1");
        String content = gson.toJson(dto);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("비밀번호 수정이 성공하는지 확인해본다")
    public void changePw() throws Exception {
        ChangePwDTO dto = new ChangePwDTO("pass1", "pass2", "pass2");
        String content = gson.toJson(dto);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/member/my/change-pw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
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

