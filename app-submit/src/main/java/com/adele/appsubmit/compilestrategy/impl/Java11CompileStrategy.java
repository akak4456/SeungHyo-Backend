package com.adele.appsubmit.compilestrategy.impl;
import com.adele.appsubmit.ExecuteResultConsumer;
import com.adele.appsubmit.compilestrategy.CompileStrategy;
import com.adele.appsubmit.compilestrategy.timeoutprocess.ProcessTimeoutException;
import com.adele.appsubmit.compilestrategy.timeoutprocess.TimeoutProcess;
import com.adele.appsubmit.compilestrategy.timeoutprocess.TimeoutProcessBuilder;
import com.adele.appsubmit.properties.CompilerConfigProperties;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.RuntimeErrorReason;
import com.adele.domainproblem.domain.ProblemInput;
import com.adele.domainproblem.domain.ProblemOutput;
import com.adele.domainproblem.dto.CompileResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Java11CompileStrategy implements CompileStrategy {
    private final String java11Path;
    private File tempDir;
    private File sourceFile;

    public Java11CompileStrategy(CompilerConfigProperties compilerConfigProperties) {
        this.java11Path = compilerConfigProperties.getJava11Path();
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
            List<ProblemInput> inputs,
            List<ProblemOutput> outputs,
            Long timeoutInMillis,
            Long memoryLimitInMegabyte,
            ExecuteResultConsumer consumer
    ) {
        List<CompileResultDTO> results = new ArrayList<>();
        for(int idx = 0; idx < inputs.size(); idx++) {
            String input = inputs.get(idx).getInputSource();
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
                CompileStatus statusResult = CompileStatus.WRONG;
                if(output.trim().equals(outputs.get(idx).getOutputSource().trim())) {
                    statusResult = CompileStatus.CORRECT;
                }
                CompileResultDTO result = new CompileResultDTO(statusResult, inputs.get(idx), outputs.get(idx),output, null, null, null);
                results.add(result);
                consumer.consume(idx, result);
            }
            catch (ProcessTimeoutException e) {
                log.error("timeout occur", e);
                CompileResultDTO result = new CompileResultDTO(CompileStatus.RUNTIME_ERROR, inputs.get(idx),outputs.get(idx),"", e, null, RuntimeErrorReason.TIMEOUT);
                results.add(result);
                consumer.consume(idx, result);
            }
            catch (IOException e) {
                log.error("ioexception occur", e);
                CompileResultDTO result = new CompileResultDTO(CompileStatus.IO_ERROR, inputs.get(idx),outputs.get(idx),"", e, null, null);
                results.add(result);
                consumer.consume(idx, result);
            }
            catch (RuntimeException | InterruptedException e) {
                log.info("runtime error occur {}", e.getMessage());
                RuntimeErrorReason reason = RuntimeErrorReason.ETC;
                if(e.getMessage().contains("java.lang.OutOfMemoryError")) {
                    reason = RuntimeErrorReason.MEMORY_EXCEED;
                }
                CompileResultDTO result = new CompileResultDTO(CompileStatus.RUNTIME_ERROR, inputs.get(idx),outputs.get(idx),"", e, null, reason);
                results.add(result);
                consumer.consume(idx, result);
            }
        }
        return results;
    }

    private String getProcessOutput(InputStream input) {
        try {
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
        } catch (IOException e) {
            // fedora(linux) jdk 는 윈도우 jdk 랑 다르게 동작해서
            // timeout 시에 process 가 종료 되면 (line = reader.readLine()) != null
            // 에 io exception 이 발생함. 그래서 하게 된 조치임
            // TODO 이 부분에 대해서 생각해보기
            log.error("ioexception occur maybe process closed", e);
            return "";
        }
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
            log.info("clean up done");
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
