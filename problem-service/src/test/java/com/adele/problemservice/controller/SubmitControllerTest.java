package com.adele.problemservice.controller;

import com.adele.common.AuthHeaderConstant;
import com.adele.problemservice.DotenvTestExecutionListener;
import com.adele.problemservice.TestConfig;
import com.adele.problemservice.compilestrategy.impl.Java11CompileStrategy;
import com.adele.problemservice.domain.Problem;
import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.CompileResultDTO;
import com.adele.problemservice.dto.ConditionDTO;
import com.adele.problemservice.dto.NewSubmitRequestDTO;
import com.adele.problemservice.dto.NewSubmitResultDTO;
import com.adele.problemservice.repository.ProblemGradeRepository;
import com.adele.problemservice.repository.ProblemRepository;
import com.adele.problemservice.repository.ProgramLanguageRepository;
import com.adele.problemservice.repository.SubmitRepository;
import com.adele.problemservice.service.CompileService;
import com.adele.problemservice.service.impl.SubmitServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = SubmitController.class)
@SpringJUnitConfig(TestConfig.class)
public class SubmitControllerTest {
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
    private CompileService compileService;

    @MockBean
    private ApplicationContext applicationContext;

    @MockBean
    private Java11CompileStrategy java11CompileStrategy;

    @Test
    @DisplayName("새로운 submit이 정상 실행되는지 확인한다.")
    public void newSubmit() throws Exception {
        NewSubmitRequestDTO dto = new NewSubmitRequestDTO(1L,"JAVA_11", "ALL", "sourcecode");
        String content = gson.toJson(dto);
        when(problemRepository.getReferenceById(any())).thenReturn(new Problem());
        when(submitService.tryNewSubmit("user1",dto)).thenReturn(new NewSubmitResultDTO(true, new SubmitList()));
        when(compileService.getCondition(dto.getProblemNo(), dto.getLangCode())).thenReturn(new ConditionDTO(List.of(), List.of(), BigDecimal.ONE, BigDecimal.ONE));
        Executor executor = Executors.newFixedThreadPool(10);  // Executor 생성

        given(compileService.compileAndRun(
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
                        post("/api/v1/submit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .header(AuthHeaderConstant.AUTH_USER, "user1")
                );

        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
}
