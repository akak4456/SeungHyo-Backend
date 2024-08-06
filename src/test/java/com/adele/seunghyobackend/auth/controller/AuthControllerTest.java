package com.adele.seunghyobackend.auth.controller;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.auth.dto.JoinDTO;
import com.adele.seunghyobackend.auth.dto.LoginDTO;
import com.adele.seunghyobackend.security.JwtTokenProvider;
import com.adele.seunghyobackend.auth.service.AuthService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import redis.embedded.RedisServer;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

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
        LoginDTO loginDTO = new LoginDTO("user1", "pass1");
        String content = gson.toJson(loginDTO);
        Authentication atc = new TestingAuthenticationToken("user1", null, "ROLE_ADMIN");
        when(authService.login("user1", "pass1")).thenReturn(jwtTokenProvider.generateToken(atc));
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("로그인이 실패하는지 확인해본다.")
    public void loginFailTest() throws Exception {
        LoginDTO loginDTO = new LoginDTO("user1", "pass1");
        String content = gson.toJson(loginDTO);
        when(authService.login("user1", "pass1")).thenThrow(BadCredentialsException.class);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
        actions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("-1"));
    }

    @Test
    @DisplayName("회원가입이 되는지 확인해본다.")
    public void joinTest() throws Exception {
        JoinDTO joinDTO = new JoinDTO("user1", "pass1","pass1", "status1", "email1");
        String content = gson.toJson(joinDTO);

        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/auth/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
