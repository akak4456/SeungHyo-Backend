package com.adele.seunghyobackend.my.controller;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.auth.controller.AuthController;
import com.adele.seunghyobackend.auth.dto.JoinDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditDTO;
import com.adele.seunghyobackend.my.service.MyService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MyController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class MyControllerTest {
    @MockBean
    private MyService myService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;

    @Test
    @DisplayName("info-edit 조회가 성공하는지 확인해본다")
    @WithMockUser(username="user1", password="pass1")
    public void getInfoEdit() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("info-edit 수정이 성공하는지 확인해본다")
    @WithMockUser(username="user1", password="pass1")
    public void patchInfoEdit() throws Exception {
        PatchInfoEditDTO dto = new PatchInfoEditDTO("user1", "pass1", "status1", "email1");
        String content = gson.toJson(dto);
        ResultActions actions =
                mockMvc.perform(
                        patch("/api/v1/my/info-edit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
