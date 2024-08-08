package com.adele.seunghyobackend.compile.service;

import com.adele.seunghyobackend.DotenvTestExecutionListener;
import com.adele.seunghyobackend.compile.CompileStatus;
import com.adele.seunghyobackend.compile.ExecuteResultConsumer;
import com.adele.seunghyobackend.compile.dto.CompileResultDTO;
import com.adele.seunghyobackend.compile.service.impl.CompileServiceImpl;
import com.adele.seunghyobackend.compile.strategy.impl.Java11CompileStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.adele.seunghyobackend.Constant.CLEAN_UP_DONE_MESSAGE;
import static com.adele.seunghyobackend.TestConstant.INTEGRATED_TAG;
import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 이 코드를 테스트 할 때
 * 환경변수 COMPILER_JAVA11_PATH 확인할것
 */
@Slf4j
@SpringBootTest
@TestExecutionListeners(listeners = {
        DotenvTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class
})
@ActiveProfiles("dev")
@Tag(INTEGRATED_TAG)
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
        String input = "";
        String expectedOutput = "Hello, World!" + System.lineSeparator();

        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                1000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.SUCCESS, result.getStatus());
        assertEquals(expectedOutput, result.getOutput());
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
        String input = "";

        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                1_000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.COMPILE_ERROR, result.getStatus());
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
        String input = "";

        // 비동기 메서드 호출
        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                List.of(input),
                1000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
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
        List<String> input = List.of("1 2", "3 4");

        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                input,
                1_000L,
                128L,
                (idx, output) -> {

        });

        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.SUCCESS, result.getStatus());
        assertEquals("3" + System.lineSeparator(), result.getOutput());
        assertNull(result.getError());

        CompileResultDTO result2 = futureResult.get().get(1);
        assertEquals(CompileStatus.SUCCESS, result2.getStatus());
        assertEquals("7" + System.lineSeparator(), result2.getOutput());
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
        List<String> input = List.of("");

        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                input,
                1_000L,
                128L,
                (idx, output) -> {

        });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
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
        List<String> input = List.of("");

        CompletableFuture<List<CompileResultDTO>> futureResult = compileService.compileAndRun(
                java11CompileStrategy,
                sourceCode,
                input,
                10_000L,
                10L,
                (idx, output) -> {

                });

        // 결과 검증
        CompileResultDTO result = futureResult.get().get(0);
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
        assertNotNull(result.getError()); // 에러 메시지가 비어 있지 않음을 확인
        log.info(result.getError().getMessage());

        verify(java11CompileStrategy, times(1)).releaseResources();
    }
}
