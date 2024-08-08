package com.adele.seunghyobackend.compile.service.impl;

import com.adele.seunghyobackend.compile.CompileStatus;
import com.adele.seunghyobackend.compile.ExecuteResultConsumer;
import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.dto.ConditionDTO;
import com.adele.seunghyobackend.compile.service.CompileService;
import com.adele.seunghyobackend.compile.strategy.CompileStrategy;
import com.adele.seunghyobackend.problem.domain.ProblemCondition;
import com.adele.seunghyobackend.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
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
            List<String> input,
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
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, "", e));
            }

            try {
                log.info("compile start");
                strategy.compile();
                log.info("compile end");
            }
            catch (InterruptedException | RuntimeException e) {
                log.error("compile error", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.COMPILE_ERROR, "", e));
            }
            catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, "", e));
            }

            try {
                log.info("execute start");
                return strategy.execute(input, timeoutInMillis, memoryLimitInMegabyte, consumer);
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
