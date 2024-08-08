package com.adele.seunghyobackend.compile.strategy.impl;

import com.adele.seunghyobackend.compile.CompileStatus;
import com.adele.seunghyobackend.compile.ExecuteResultConsumer;
import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.strategy.*;
import com.adele.seunghyobackend.compile.strategy.timeoutprocess.ProcessTimeoutException;
import com.adele.seunghyobackend.compile.strategy.timeoutprocess.TimeoutProcess;
import com.adele.seunghyobackend.compile.strategy.timeoutprocess.TimeoutProcessBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(sourceCode);
            writer.flush();
        }
    }

    @Override
    public void compile() throws IOException, InterruptedException {
        // ProcessBuilder 설정
        ProcessBuilder processBuilder = new ProcessBuilder(
                java11Path + File.separator + "javac",
                sourceFile.getAbsolutePath()
        );
        processBuilder.directory(tempDir);
        processBuilder.redirectErrorStream(true); // 표준 오류를 표준 출력으로 리디렉션

        // 프로세스 시작 및 표준 출력 읽기
        Process process = processBuilder.start();
        String compileOutput = getProcessOutput(process.getInputStream());

        int compileExitCode = process.waitFor();
        if (compileExitCode != 0) {
            throw new RuntimeException("Compilation failed with error: " + compileOutput);
        }
    }

    @Override
    public List<CompileResultDTO> execute(
            List<String> inputs,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer
    ) {
        List<CompileResultDTO> results = new ArrayList<>();
        for(int idx = 0; idx < inputs.size(); idx++) {
            String input = inputs.get(idx);
            try {
                TimeoutProcessBuilder processBuilder = new TimeoutProcessBuilder(
                        timeoutInMillis,
                        java11Path + File.separator + "java",
                        "-cp",
                        tempDir.getAbsolutePath(),
                        "-Xmx" + memoryLimitInMegabyte +"m",
                        "Main"
                );
                processBuilder.directory(tempDir);
                processBuilder.redirectErrorStream(true); // 표준 오류를 표준 출력으로 리디렉션
                // 프로세스의 표준 입력 스트림에 데이터 쓰기
                TimeoutProcess process = processBuilder.start();
                try (OutputStream os = process.getOutputStream()) {
                    os.write(input.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                // 프로세스의 표준 출력 읽기
                String output = getProcessOutput(process.getInputStream());
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Process exited with code " + exitCode + " and output: " + output);
                }
                CompileResultDTO result = new CompileResultDTO(CompileStatus.SUCCESS, output, null);
                results.add(result);
                consumer.consume(idx, result);
            } catch (IOException e) {
                CompileResultDTO result = new CompileResultDTO(CompileStatus.IO_ERROR, "", e);
                results.add(result);
                consumer.consume(idx, result);
            }
            catch (ProcessTimeoutException e) {
                log.error("timeout occur", e);
                CompileResultDTO result = new CompileResultDTO(CompileStatus.RUNTIME_ERROR, "", e);
                results.add(result);
                consumer.consume(idx, result);
            }
            catch (RuntimeException | InterruptedException e) {
                CompileResultDTO result = new CompileResultDTO(CompileStatus.RUNTIME_ERROR, "", e);
                results.add(result);
                consumer.consume(idx, result);
            }
        }
        return results;
    }

    private String getProcessOutput(InputStream input) throws IOException {
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append(System.lineSeparator());
            }
            output = outputBuilder.toString();
        }
        return output;
    }

    @Override
    public void releaseResources() {
        try {
            if(!deleteDirectory(tempDir)) {
                throw new IOException("Failed to delete temp directory");
            }
        } catch (IOException e) {
            log.error("Failed to delete temp directory: {}", e.getMessage(), e);
        } finally {
            log.info(CLEAN_UP_DONE_MESSAGE);
        }
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        boolean result = true;
        if (allContents != null) {
            for (File file : allContents) {
                result = result && deleteDirectory(file);
            }
        }
        return result && directoryToBeDeleted.delete();
    }
}
