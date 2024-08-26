package com.adele.problemservice.controller;

import com.adele.domainproblem.service.ProgramLanguageService;
import com.adele.problemservice.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramLanguageController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
public class ProgramLanguageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramLanguageService programLanguageService;

    @Test
    @DisplayName("문제 번호에 맞는 program language 테스트")
    @WithMockUser
    public void addGetOne() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/programlanguage/1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 언어들 잘 얻어오는지 테스트")
    @WithMockUser
    public void getAllLanguages() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/programlanguage")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }
}
