package com.adele.problemservice.service.impl;

import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.ExecuteResultConsumer;
import com.adele.problemservice.compilestrategy.CompileStrategy;
import com.adele.problemservice.domain.ProblemCondition;
import com.adele.problemservice.domain.ProblemInput;
import com.adele.problemservice.domain.ProblemOutput;
import com.adele.problemservice.dto.CompileResultDTO;
import com.adele.problemservice.dto.ConditionDTO;
import com.adele.problemservice.repository.ProblemRepository;
import com.adele.problemservice.service.CompileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompileServiceImpl implements CompileService {

    private final ProblemRepository problemRepository;

    @Async
    @Override
    public CompletableFuture<List<CompileResultDTO>> compileAndRun(
            CompileStrategy strategy,
            String sourceCode,
            List<ProblemInput> input,
            List<ProblemOutput> output,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("write source code start");
                strategy.writeSourceCode(sourceCode);
                log.info("write source code end");
            } catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, null,null,"", e));
            }

            try {
                log.info("compile start");
                strategy.compile();
                log.info("compile end");
            }
            catch (InterruptedException | RuntimeException e) {
                log.error("compile error", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.COMPILE_ERROR, null,null,"", e));
            }
            catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, null,null,"", e));
            }

            try {
                log.info("execute start");
                return strategy.execute(input, output,timeoutInMillis, memoryLimitInMegabyte, consumer);
            } finally {
                strategy.releaseResources();
                log.info("execute end");
            }
        });
    }

    @Override
    public ConditionDTO getCondition(Long problemNo, String langCode) {
        List<ProblemCondition> conditions = problemRepository.findByIdWithCondition(problemNo);
        ProblemCondition condition = null;
        for (ProblemCondition c : conditions) {
            if(c.getLanguage().getLangCode().equals(langCode)) {
                condition = c;
                break;
            }
        }
        return new ConditionDTO(
                problemRepository.findByIdWithInput(problemNo),
                problemRepository.findByIdWithOutput(problemNo),
                condition.getConditionTime(),
                condition.getConditionMemory()
        );
    }

}
