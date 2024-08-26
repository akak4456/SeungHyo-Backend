package com.adele.problemservice.controller;

import com.adele.domainproblem.service.SubmitService;
import com.adele.problemservice.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = SubmitController.class)
@SpringJUnitConfig(TestConfig.class)
public class SubmitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmitService submitService;

    @Test
    @DisplayName("문제 채점 결과를 얻어오는지 확인한다")
    @WithMockUser
    public void getProblemGradeOne() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/submit/1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("오답노트 결과들을 얻어오는지 확인한다")
    @WithMockUser
    public void getReflectionNoteList() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/submit")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }
}
