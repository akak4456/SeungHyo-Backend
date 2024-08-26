package com.adele.appsubmit.executor;

import com.adele.appsubmit.ExecuteResultConsumer;
import com.adele.appsubmit.compilestrategy.CompileStrategy;
import com.adele.domainproblem.CompileErrorReason;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import com.adele.domainproblem.dto.CompileResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component

public interface CompileExecutor {

    @Async
    CompletableFuture<List<CompileResultDTO>> compileAndRun(
            CompileStrategy strategy,
            String sourceCode,
            List<ProblemInput> input,
            List<ProblemOutput> output,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer
    );
}
