package com.adele.seunghyobackend.problem.controller;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.problem.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProblemListController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class ProblemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProblemService problemService;

    @Test
    @DisplayName(" api 가 정상 작동하는지 확인해본다")
    public void getPageTest() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/problem")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        ResultActions actions2 =
                mockMvc.perform(
                        get("/api/v1/problem?page=0&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions2
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        ResultActions actions3 =
                mockMvc.perform(
                        get("/api/v1/problem?page=1&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions3
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
