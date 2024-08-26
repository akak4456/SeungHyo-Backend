package com.adele.appsubmit.compilestrategy;



import com.adele.appsubmit.ExecuteResultConsumer;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import com.adele.domainproblem.dto.CompileResultDTO;

import java.io.IOException;
import java.util.List;

/**
 * 주의!!! 상태를 가지고 있으므로
 * Bean 으로 관리하지 말고 new instant 생성자를 이용하도록 하자
 */
public interface CompileStrategy {
    void writeSourceCode(String sourceCode) throws IOException;

    void compile() throws IOException, InterruptedException;

    List<CompileResultDTO> execute(
            List<ProblemInput> input,
            List<ProblemOutput> outputs,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer);

    void releaseResources();
}
