package com.adele.seunghyobackend.compile.service;

import com.adele.seunghyobackend.DotenvTestExecutionListener;
import com.adele.seunghyobackend.compile.CompileStatus;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.adele.seunghyobackend.Constant.CLEAN_UP_DONE_MESSAGE;
import static com.adele.seunghyobackend.TestConstant.INTEGRATED_TAG;
import static com.adele.seunghyobackend.TestConstant.UNIT_TEST_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        CompletableFuture<CompileResultDTO> futureResult = compileService.compileAndRun(java11CompileStrategy, sourceCode, input);

        // 결과 검증
        CompileResultDTO result = futureResult.get();
        assertEquals(CompileStatus.SUCCESS, result.getStatus());
        assertEquals(expectedOutput, result.getOutput());
        assertEquals("", result.getError());

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
        CompletableFuture<CompileResultDTO> futureResult = compileService.compileAndRun(java11CompileStrategy, sourceCode, input);

        // 결과 검증
        CompileResultDTO result = futureResult.get();
        assertEquals(CompileStatus.COMPILE_ERROR, result.getStatus());
        assertNotEquals("", result.getError()); // 에러 메시지가 비어 있지 않음을 확인

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
        CompletableFuture<CompileResultDTO> futureResult = compileService.compileAndRun(java11CompileStrategy, sourceCode, input);

        // 결과 검증
        CompileResultDTO result = futureResult.get();
        assertEquals(CompileStatus.RUNTIME_ERROR, result.getStatus());
        assertNotEquals("", result.getError()); // 에러 메시지가 비어 있지 않음을 확인

        verify(java11CompileStrategy, times(1)).releaseResources();
    }
}
