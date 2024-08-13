package com.adele.boardservice.controller;

import com.adele.boardservice.TestConfig;
import com.adele.boardservice.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Test
    @DisplayName("list api 가 정상 작동하는지 확인해본다")
    public void getPageTest() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/board")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        ResultActions actions2 =
                mockMvc.perform(
                        get("/api/v1/board?page=0&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions2
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        ResultActions actions3 =
                mockMvc.perform(
                        get("/api/v1/board?page=1&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions3
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    @DisplayName("하나 조회 가 정상 작동하는지 확인해본다")
    public void getOne() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/board/1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
