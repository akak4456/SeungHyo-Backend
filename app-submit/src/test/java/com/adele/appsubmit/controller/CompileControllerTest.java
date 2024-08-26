package com.adele.appsubmit.controller;

import com.adele.appsubmit.ExecuteResultConsumer;
import com.adele.appsubmit.TestConfig;
import com.adele.appsubmit.compilestrategy.CompileStrategy;
import com.adele.appsubmit.compilestrategy.impl.Java11CompileStrategy;
import com.adele.appsubmit.executor.CompileExecutor;
import com.adele.appsubmit.properties.CompilerConfigProperties;
import com.adele.domainproblem.domain.Problem;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import com.adele.domainproblem.dto.*;
import com.adele.domainproblem.repository.ProblemGradeRepository;
import com.adele.domainproblem.repository.ProblemRepository;
import com.adele.domainproblem.repository.ProgramLanguageRepository;
import com.adele.domainproblem.repository.SubmitRepository;
import com.adele.domainproblem.service.ProblemService;
import com.adele.domainproblem.service.impl.SubmitServiceImpl;
import com.adele.internalcommon.request.AuthHeaderConstant;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = CompileController.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringJUnitConfig(TestConfig.class)
public class CompileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private SubmitServiceImpl submitService;

    @Mock
    private SubmitRepository submitRepository;
    @Mock
    private ProblemRepository problemRepository;
    @Mock
    private ProgramLanguageRepository programLanguageRepository;
    @Mock
    private ProblemGradeRepository problemGradeRepository;

    @MockBean
    private CompileExecutor compileExecutor;

    @MockBean
    private ProblemService problemService;

    @MockBean
    private KafkaTemplate<String, KafkaCompile> kafkaTemplate;

    @Test
    @DisplayName("새로운 submit이 정상 실행되는지 확인한다.")
    @WithMockUser
    public void newSubmit() throws Exception {
        NewSubmitRequestDTO dto = new NewSubmitRequestDTO(1L,"JAVA_11", "ALL", "sourcecode");
        String content = gson.toJson(dto);
        // when(problemRepository.getReferenceById(any())).thenReturn(new Problem());
        when(submitService.tryNewSubmit("user1",dto)).thenReturn(new NewSubmitResultDTO(true, 1L));
        when(problemService.getCondition(dto.getProblemNo(), dto.getLangCode())).thenReturn(new ConditionDTO(List.of(), List.of(), BigDecimal.ONE, BigDecimal.ONE));
        Executor executor = Executors.newFixedThreadPool(10);  // Executor 생성

        given(compileExecutor.compileAndRun(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).willReturn(CompletableFuture.supplyAsync(() -> {
            // 여기에 비즈니스 로직을 넣거나, 테스트 용도로 특정 값을 반환
            return List.of(new CompileResultDTO());  // 가정된 반환 타입
        }, executor));
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/compile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk());
    }
}
