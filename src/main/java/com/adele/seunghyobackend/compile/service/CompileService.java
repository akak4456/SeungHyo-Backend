package com.adele.seunghyobackend.compile.service;

import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.strategy.CompileStrategy;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface CompileService {

    CompletableFuture<CompileResultDTO> compileAndRun(CompileStrategy strategy, String sourceCode, String input) throws IOException, InterruptedException;
}
