package com.adele.appsubmit;


import com.adele.domainproblem.dto.CompileResultDTO;

@FunctionalInterface
public interface ExecuteResultConsumer {
    void consume(int idx, CompileResultDTO result);
}
