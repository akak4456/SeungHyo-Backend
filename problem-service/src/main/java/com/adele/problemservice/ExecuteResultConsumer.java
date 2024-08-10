package com.adele.problemservice;


import com.adele.problemservice.dto.CompileResultDTO;

@FunctionalInterface
public interface ExecuteResultConsumer {
    void consume(int idx, CompileResultDTO result);
}
