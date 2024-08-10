package com.adele.problemservice.service;

import com.adele.problemservice.DotenvTestExecutionListener;
import com.adele.problemservice.dto.ProblemListDTO;
import com.adele.problemservice.dto.ProblemOneDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
@Slf4j
public class ProblemServiceIntegratedTest {
    @Autowired
    private ProblemService problemService;

    @Test
    @DisplayName("page 가 정상 작동하는지 확인해본다.")
    public void pageTest() {
        Pageable pageable = PageRequest.of(0 ,10);
        Page<ProblemListDTO> result = problemService.searchPage(pageable);
        for (ProblemListDTO problem : result.getContent()) {
            log.info(problem.toString());
        }
    }

    @Test
    @DisplayName("하나 조회를 테스트해본다")
    public void getOneTest() {
        ProblemOneDTO one = problemService.problemOne(1L);
        log.info(one.toString());
    }
}
