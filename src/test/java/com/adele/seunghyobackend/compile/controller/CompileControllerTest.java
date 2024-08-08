package com.adele.seunghyobackend.compile.controller;

import com.adele.seunghyobackend.DotenvTestExecutionListener;
import com.adele.seunghyobackend.TestConfig;
import com.adele.seunghyobackend.compile.service.CompileService;
import com.adele.seunghyobackend.compile.strategy.impl.Java11CompileStrategy;
import com.adele.seunghyobackend.submit.controller.SubmitController;
import com.adele.seunghyobackend.submit.dto.NewSubmitRequestDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.adele.seunghyobackend.TestConstant.INTEGRATED_TAG;
import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
@Tag(INTEGRATED_TAG)
public class CompileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Test
    @DisplayName("/compile api 가 성공하는지 확인해본다.")
    public void compile() throws Exception {
        NewSubmitRequestDTO dto = new NewSubmitRequestDTO(1L,"JAVA_11", "ALL", "sourcecode");
        String content = gson.toJson(dto);
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/compile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
