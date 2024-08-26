package com.adele.appsubmit.executor.impl;

import com.adele.appsubmit.ExecuteResultConsumer;
import com.adele.appsubmit.compilestrategy.CompileStrategy;
import com.adele.appsubmit.executor.CompileExecutor;
import com.adele.domainproblem.CompileErrorReason;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import com.adele.domainproblem.dto.CompileResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CompileExecutorImpl implements CompileExecutor {
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
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, null,null,"", e, null, null));
            }

            try {
                log.info("compile start");
                strategy.compile();
                log.info("compile end");
            }
            catch (InterruptedException | RuntimeException e) {
                log.error("compile error", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.COMPILE_ERROR, null,null,"", e, CompileErrorReason.ETC, null));
            }
            catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return List.of(new CompileResultDTO(CompileStatus.IO_ERROR, null,null,"", e, null, null));
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
}
