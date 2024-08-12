package com.adele.problemservice.service;


import com.adele.problemservice.ExecuteResultConsumer;
import com.adele.problemservice.compilestrategy.CompileStrategy;
import com.adele.problemservice.domain.ProblemInput;
import com.adele.problemservice.domain.ProblemOutput;
import com.adele.problemservice.dto.CompileResultDTO;
import com.adele.problemservice.dto.ConditionDTO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CompileService {

    /**
     * 컴파일을 해주는 Service
     * @param strategy 컴파일 전략
     * @param sourceCode 소스코드
     * @param input 들어갈 입력
     * @param output 들어갈 출력
     * @param timeoutInMillis 타임아웃 시간(단위 밀리초)
     * @param memoryLimitInMegabyte 메모리제한(단위 MB)
     * @return CompileResultDTO 컴파일 결과
     */
    CompletableFuture<List<CompileResultDTO>> compileAndRun(
            CompileStrategy strategy,
            String sourceCode,
            List<ProblemInput> input,
            List<ProblemOutput> output,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer
    ) throws IOException, InterruptedException;

    /**
     * input, output 을 얻는 service
     * @param problemNo 얻고자 하는 문제 번호, langCode 조건 언어 코드
     * @return ConditionDTO input, output, 조건들
     */
    ConditionDTO getCondition(Long problemNo, String langCode);
    
}
