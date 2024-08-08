package com.adele.seunghyobackend.compile.controller;

import com.adele.seunghyobackend.ApiResult;
import com.adele.seunghyobackend.compile.dto.CompileRequestDTO;
import com.adele.seunghyobackend.compile.dto.ConditionDTO;
import com.adele.seunghyobackend.compile.service.CompileService;
import com.adele.seunghyobackend.compile.strategy.CompileStrategy;
import com.adele.seunghyobackend.compile.strategy.impl.Java11CompileStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.adele.seunghyobackend.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1/compile")
@RequiredArgsConstructor
@Slf4j
public class CompileController {

    private final CompileService compileService;


    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 컴파일을 시도한다. 채점은 비동기적으로 이루어진다.
     * @param requestDTO
     * <ul>
     *     <li><b>langCode</b> 채점할 언어 코드</li>
     *     <li><b>problemNo</b> 채점할 문제</li>
     *     <li><b>sourceCode</b> 소스 코드</li>
     * </ul>
     * @return List<CompileResultDTO> compile 결과들
     * <ul>
     *     <li><b>status</b> compile 결과</li>
     *     <li><b>output</b> 컴파일 정상 동작 시에 결과</li>
     *     <li><b>error</b> 컴파일 에러 정보</li>
     * </ul>
     */
    @PostMapping("")
    public ApiResult<Void> compile(@RequestBody CompileRequestDTO requestDTO) throws IOException, InterruptedException, ExecutionException {
        CompileStrategy strategy = null;
        if(requestDTO.getLangCode().equals("JAVA_11")) {
            strategy = applicationContext.getBean(Java11CompileStrategy.class);
        }
        ConditionDTO conditionDTO = compileService.getCondition(requestDTO.getProblemNo(), requestDTO.getLangCode());
        compileService.compileAndRun(
                strategy,
                requestDTO.getSourceCode(),
                conditionDTO.getInput(),
                (long) (conditionDTO.getTimeCondition().doubleValue() * 1000),
                conditionDTO.getMemoryCondition().longValue(),
                (idx, compileResult) -> {
            log.info("idx : {}, result : {}", idx, compileResult);
        }).thenAccept(result -> {
            // 전체 결과가 완료된 후 추가적인 처리 가능
            // 예: 결과를 데이터베이스에 저장
            log.info("All compile results processed");
        }).exceptionally(ex -> {
            // 예외 처리
            log.error("An error occurred during compilation: ", ex);
            throw new RuntimeException(ex);
        });;
        return ApiResult.<Void>builder()
                .code(CODE_SUCCESS)
                .message("컴파일 시도 성공")
                .build();
    }
}
