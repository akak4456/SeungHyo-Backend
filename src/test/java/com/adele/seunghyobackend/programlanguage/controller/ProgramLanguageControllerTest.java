package com.adele.seunghyobackend.programlanguage.controller;

import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.programlanguage.service.ProgramLanguageService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramLanguageController.class)
@SpringJUnitConfig(TestConfig.class)
@Slf4j
@Tag(UNIT_TEST_TAG)
public class ProgramLanguageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramLanguageService programLanguageService;

    @Test
    @DisplayName("문제 번호에 맞는 program language 테스트")
    public void addGetOne() throws Exception {
        ResultActions actions =
                mockMvc.perform(
                        get("/api/v1/programlanguage/1")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
