package com.adele.seunghyobackend.compile.strategy.impl;

import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.strategy.CompileStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.adele.seunghyobackend.Constant.CLEAN_UP_DONE_MESSAGE;

@Scope("prototype")
@Component
@Slf4j
public class Java11CompileStrategy implements CompileStrategy {
    private final String java11Path;
    private File tempDir;
    private File sourceFile;

    public Java11CompileStrategy(@Value("${compiler.java11-path}") String java11Path) {
        this.java11Path = java11Path;
    }
    @Override
    public void writeSourceCode(String sourceCode) throws IOException {
        String uniqueID = UUID.randomUUID().toString();
        tempDir = new File(System.getProperty("java.io.tmpdir"), uniqueID);
        sourceFile = new File(tempDir, "Main.java");

        if (!tempDir.mkdir()) {
            throw new IOException("Failed to create temp directory");
        }
        FileUtils.writeStringToFile(sourceFile, sourceCode, StandardCharsets.UTF_8);
    }

    @Override
    public void compile() throws IOException {
        ByteArrayOutputStream compileOutput = new ByteArrayOutputStream();
        CommandLine compileCmd = new CommandLine(java11Path + "\\javac");
        compileCmd.addArgument(sourceFile.getAbsolutePath());

        DefaultExecutor compileExecutor = new DefaultExecutor();
        compileExecutor.setWorkingDirectory(tempDir);
        compileExecutor.setStreamHandler(new PumpStreamHandler(compileOutput));

        int compileExitCode = compileExecutor.execute(compileCmd);

        if (compileExitCode != 0) {
            String compileError = compileOutput.toString(StandardCharsets.UTF_8);
            throw new ExecuteException(compileError, compileExitCode);
        }
    }

    @Override
    public String execute() throws IOException {
        ByteArrayOutputStream runOutput = new ByteArrayOutputStream();
        CommandLine runCmd = new CommandLine(java11Path + "\\java");
        runCmd.addArgument("-cp");
        runCmd.addArgument(tempDir.getAbsolutePath());
        runCmd.addArgument("Main");

        DefaultExecutor runExecutor = new DefaultExecutor();
        runExecutor.setWorkingDirectory(tempDir);
        runExecutor.setStreamHandler(new PumpStreamHandler(runOutput));

        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000); // 60초 타임아웃 설정
        runExecutor.setWatchdog(watchdog);

        int runExitCode = runExecutor.execute(runCmd);

        String output = runOutput.toString(StandardCharsets.UTF_8);
        if (runExitCode != 0) {
            throw new ExecuteException(output, runExitCode);
        }
        return output;
    }

    @Override
    public void releaseResources() {
        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            log.error("Failed to delete temp directory: {}", e.getMessage(), e);
        } finally {
            log.info(CLEAN_UP_DONE_MESSAGE);
        }
    }
}
