package com.adele.seunghyobackend.compile;

import com.adele.seunghyobackend.compile.dto.CompileResultDTO;

@FunctionalInterface
public interface ExecuteResultConsumer {
    void consume(int idx, CompileResultDTO result);
}
