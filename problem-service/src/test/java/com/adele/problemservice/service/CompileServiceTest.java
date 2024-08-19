package com.adele.problemservice.service;

import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.RuntimeErrorReason;
import com.adele.problemservice.TestConfig;
import com.adele.problemservice.compilestrategy.impl.Java11CompileStrategy;
import com.adele.problemservice.domain.Problem;
import com.adele.problemservice.domain.ProblemInput;
import com.adele.problemservice.domain.ProblemOutput;
import com.adele.problemservice.dto.CompileResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 이 코드를 테스트 할 때
 * 환경변수 COMPILER_JAVA11_PATH 확인할것
 */
@Slf4j
@SpringJUnitConfig(TestConfig.class)
public class CompileServiceTest {
    @Autowired
    private CompileService compileService;
    
    @Autowired
    private ApplicationContext applicationContext;

    private Java11CompileStrategy java11CompileStrategy;

    @BeforeEach
    public void setUp() {
        java11CompileStrategy = Mockito.spy(applicationContext.getBean(Java11CompileStrategy.class));
    }

    @Test
    @DisplayName("컴파일이 정상적으로 되는지 확인해본다.")
    public void testCompileAndRunSuccess() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        // 테스트할 소스 코드
        String sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); }}";
        ProblemInput input = new ProblemInput(1L, true, new Problem(), "");
        ProblemOutput expectedOutput = new ProblemOutput(1L, true, new Problem(),"Hello, World!");

        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                List.of(expectedOutput),
                1000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.CORRECT, result.getStatus());
        assertEquals(expectedOutput, result.getExpectedOutput());
        assertNull(result.getError());

        verify(java11CompileStrategy, times(1)).releaseResources();
    }

    @Test
    @DisplayName("컴파일 에러가 나는지 확인해본다.")
    public void testCompileError() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        // 컴파일 오류를 발생시키는 소스 코드
        String sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\" }"; // Syntax error
        ProblemInput input = new ProblemInput(1L, true, new Problem(), "");
        ProblemOutput expectedOutput = new ProblemOutput(1L, true, new Problem(),"");

        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                List.of(expectedOutput),
                1_000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.COMPILE_ERROR, result.getStatus());
        assertThat(result.getCompileErrorReason()).isEqualTo(result.getCompileErrorReason());
        assertNotNull(result.getError()); // 에러 메시지가 비어 있지 않음을 확인

        verify(java11CompileStrategy, times(1)).releaseResources();
    }

    @Test
    @DisplayName("런타임 에러가 발생하는지 확인해본다.")
    public void testRuntimeError() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        // 런타임 오류를 발생시키는 소스 코드
        String sourceCode = "public class Main { public static void main(String[] args) { int x = 1 / 0; }}"; // Division by zero
        ProblemInput input = new ProblemInput(1L, true, new Problem(), "");
        ProblemOutput expectedOutput = new ProblemOutput(1L, true, new Problem(),"");


        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                List.of(expectedOutput),
                1000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
        assertEquals(RuntimeErrorReason.ETC, result.getRuntimeErrorReason());
        assertNotNull(result.getError()); // 에러 메시지가 비어 있지 않음을 확인

        verify(java11CompileStrategy, times(1)).releaseResources();
    }

    @Test
    @DisplayName("ADD 가 정상적으로 되는지 확인해본다")
    public void addTest() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String sourceCode = """
                import java.io.*;
                import java.util.*;
                
                public class Main {
                    public static void main(String[] args) throws IOException {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        StringTokenizer tokenizer = new StringTokenizer(reader.readLine(), " ");
                        int a = Integer.parseInt(tokenizer.nextToken());
                        int b = Integer.parseInt(tokenizer.nextToken());
                        System.out.println(a + b);
                    }
                }
                """;

        List<ProblemInput> input = List.of(new ProblemInput(1L, true, new Problem(), "1 2"), new ProblemInput(1L, true, new Problem(), "3 4"));
        List<ProblemOutput> expectedOutput = List.of(new ProblemOutput(1L, true, new Problem(), "3"), new ProblemOutput(1L, true, new Problem(), "7"));
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                input,
                expectedOutput,
                1_000L,
                128L,
                (idx, output) -> {

        });

        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.CORRECT, result.getStatus());
        assertEquals("3" + System.lineSeparator(), result.getCompileOutput());
        assertNull(result.getError());

        CompileResultDTO result2 = futureResult.get().get(1);
        assertEquals(CompileStatus.CORRECT, result2.getStatus());
        assertEquals("7" + System.lineSeparator(), result2.getCompileOutput());
        assertNull(result2.getError());

        verify(java11CompileStrategy, times(1)).releaseResources();

    }

    @Test
    @DisplayName("무한 루프가 발생하게 된다면?")
    public void infiniteLoop() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String sourceCode = """
                import java.io.*;
                import java.util.*;
                
                public class Main {
                    public static void main(String[] args) throws IOException {
                        while(true) {
                            System.out.println("A");
                        }
                    }
                }
                """;
        ProblemInput input = new ProblemInput(1L, true, new Problem(), "");
        ProblemOutput expectedOutput = new ProblemOutput(1L, true, new Problem(),"");

        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                List.of(expectedOutput),
                1_000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
        assertEquals(RuntimeErrorReason.TIMEOUT, result.getRuntimeErrorReason());
        assertNotNull(result.getError()); // 에러 메시지가 비어 있지 않음을 확인

        verify(java11CompileStrategy, times(1)).releaseResources();
    }

    @Test
    @DisplayName("메모리가 초과된다면?(힙 메모리)")
    public void memoryOver() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String sourceCode = """
                import java.io.*;
                import java.util.*;
                
                public class Main {
                    public static void main(String[] args) throws IOException {
                        ArrayList<Object> objects = new ArrayList<>();
                        while(true) {
                            objects.add(new Object());
                        }
                    }
                }
                """;
        ProblemInput input = new ProblemInput(1L, true, new Problem(), "");
        ProblemOutput expectedOutput = new ProblemOutput(1L, true, new Problem(),"");


        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                List.of(expectedOutput),
                10_000L,
                10L,
                (idx, output) -> {

                });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
        assertEquals(RuntimeErrorReason.MEMORY_EXCEED, result.getRuntimeErrorReason());
        assertNotNull(result.getError()); // 에러 메시지가 비어 있지 않음을 확인
        log.info(result.getError().getMessage());

        verify(java11CompileStrategy, times(1)).releaseResources();
    }

    @Test
    @DisplayName("만약 output 이 여러줄이라면?")
    public void multipleLineOutput() throws IOException, InterruptedException, ExecutionException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String sourceCode = """
                import java.io.*;
                import java.util.*;
                
                public class Main {
                    public static void main(String[] args) throws IOException {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        StringTokenizer tokenizer = new StringTokenizer(reader.readLine(), " ");
                        int a = Integer.parseInt(tokenizer.nextToken());
                        int b = Integer.parseInt(tokenizer.nextToken());
                        System.out.println(a + b);
                        System.out.println(a * b);
                    }
                }
                """;

        List<ProblemInput> input = List.of(new ProblemInput(1L, true, new Problem(), "1 2"), new ProblemInput(1L, true, new Problem(), "3 4"));
        List<ProblemOutput> expectedOutput = List.of(new ProblemOutput(1L, true, new Problem(), "3" + System.lineSeparator() +"2"), new ProblemOutput(1L, true, new Problem(), "7" + System.lineSeparator() + "12"));
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                input,
                expectedOutput,
                1_000L,
                128L,
                (idx, output) -> {

                });

        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.CORRECT, result.getStatus());
        assertNull(result.getError());

        CompileResultDTO result2 = futureResult.get().get(1);
        assertEquals(CompileStatus.CORRECT, result2.getStatus());
        assertNull(result2.getError());

        verify(java11CompileStrategy, times(1)).releaseResources();

    }
}
