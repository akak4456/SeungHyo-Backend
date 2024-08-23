package com.adele.boardservice.controller;

import com.adele.boardservice.TestConfig;
import com.adele.domainboard.dto.BoardWriteDTO;
import com.adele.domainboard.dto.BoardWriteResultDTO;
import com.adele.domainboard.dto.ProblemDTO;
import com.adele.domainboard.service.BoardService;
import com.adele.internalcommon.request.AuthHeaderConstant;
import com.adele.internalcommon.response.ApiResponse;
import com.adele.internalcommon.response.EmptyResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @WithMockUser
    public void getPageTest() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/board")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());

        ResultActions actions2 =
                mockMvc.perform(
                        get("/api/v1/board?page=0&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions2
                .andExpect(status().isOk());

        ResultActions actions3 =
                mockMvc.perform(
                        get("/api/v1/board?page=1&size=10")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions3
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("하나 조회 가 정상 작동하는지 확인해본다")
    @WithMockUser
    public void getOne() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/board/1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시판 카테고리 조회 가 정상 작동하는지 확인해본다")
    @WithMockUser
    public void getCategories() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/board/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("게시판 등록이 정상 작동하는지 확인해본다")
    @MethodSource("provideBoard")
    @Disabled
    public void addBoard(BoardWriteDTO writeDTO, BoardWriteResultDTO writeResultDTO, ProblemDTO problemDTO) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(writeDTO);
        when(boardService.getProblemOne(any())).thenReturn(problemDTO);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/board")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                                .content(json)
                );

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();
        Type apiResultType = new TypeToken<ApiResponse<BoardWriteResultDTO>>() {}.getType();

        ApiResponse<BoardWriteResultDTO> response = gson.fromJson(responseJson, apiResultType);
        log.info(response.getData().toString());
        assertThat(response.getData()).isEqualTo(writeResultDTO);
    }

    private static Stream<Arguments> provideBoard() {
        return Stream.of(
                Arguments.of(
                        new BoardWriteDTO(
                                "", "","", "", "", "a", "",""
                        ),
                        new EmptyResponse(),
                        new ProblemDTO()
                ),
                Arguments.of(
                        new BoardWriteDTO(
                                "a", "a","a", "a", "a", "a", "a","a"
                        ),
                        new EmptyResponse(),
                        new ProblemDTO()
                ),
                Arguments.of(
                        new BoardWriteDTO(
                                "a", "a","a", "a", "a", "1", "a","a"
                        ),
                        new BoardWriteResultDTO(
                                "","","","","","","","", true
                        ),
                        new ProblemDTO("A+B")
                )
        );
    }
}
