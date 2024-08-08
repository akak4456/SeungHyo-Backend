package com.adele.seunghyobackend.submit;

import com.adele.seunghyobackend.submit.dto.CompileResultDTO;

@FunctionalInterface
public interface ExecuteResultConsumer {
    void consume(int idx, CompileResultDTO result);
}
