package com.adele.seunghyobackend.compile.service.impl;

import com.adele.seunghyobackend.compile.CompileStatus;
import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.service.CompileService;
import com.adele.seunghyobackend.compile.strategy.CompileStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class CompileServiceImpl implements CompileService {

    @Async
    @Override
    public CompletableFuture<CompileResultDTO> compileAndRun(CompileStrategy strategy, String sourceCode, String input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                strategy.writeSourceCode(sourceCode);
            } catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return new CompileResultDTO(CompileStatus.IO_ERROR, "", e.getMessage());
            }

            try {
                strategy.compile();
            }
            catch (ExecuteException e) {
                strategy.releaseResources();
                return new CompileResultDTO(CompileStatus.COMPILE_ERROR, "", e.getMessage());
            }
            catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                strategy.releaseResources();
                return new CompileResultDTO(CompileStatus.IO_ERROR, "", e.getMessage());
            }

            try {
                String output = strategy.execute();
                return new CompileResultDTO(CompileStatus.SUCCESS, output, "");
            } catch (ExecuteException e) {
                return new CompileResultDTO(CompileStatus.RUNTIME_ERROR, "", e.getMessage());
            } catch (IOException e) {
                log.error("application.yml COMPILER_JAVA11_PATH 확인해볼것", e);
                return new CompileResultDTO(CompileStatus.IO_ERROR, "", e.getMessage());
            } finally {
                strategy.releaseResources();
            }
        });
    }

}
