package com.adele.problemservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import com.adele.problemservice.compilestrategy.CompileStrategy;
import com.adele.problemservice.compilestrategy.impl.Java11CompileStrategy;
import com.adele.problemservice.dto.ConditionDTO;
import com.adele.problemservice.dto.NewSubmitRequestDTO;
import com.adele.problemservice.dto.NewSubmitResultDTO;
import com.adele.problemservice.service.CompileService;
import com.adele.problemservice.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/submit")
@RequiredArgsConstructor
@Slf4j
public class SubmitController {
    private final SubmitService submitService;

    private final CompileService compileService;


    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    /**
     * 소스코드 새로운 제출을 시도한다
     * @param newSubmitRequestDTO
     * <ul>
     *     <li><b>problemNo</b> 해당하는 문제 번호</li>
     *     <li><b>langCode</b> 제출한 언어 코드</li>
     *     <li><b>sourceCodeDisclosureScope</b> 소스코드 공개 범위 SourceCodeDisclosureScope class 의 enum value 를 따름 @see SourceCodeDisclosureScope</li>
     *     <li><b>sourceCode</b> 소스 코드</li>
     * </ul>
     * @return NewSubmitResultDTO
     * 제출 시도 성공했는지 여부
     */
    @PostMapping("")
    public ApiResult<NewSubmitResultDTO> newSubmit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody NewSubmitRequestDTO newSubmitRequestDTO) throws IOException, InterruptedException {
        NewSubmitResultDTO result = submitService.tryNewSubmit(memberId, newSubmitRequestDTO);
        tryCompile(newSubmitRequestDTO);
        return ApiResult.<NewSubmitResultDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("제출 시도 성공")
                .data(result)
                .build();
    }

    private void tryCompile(NewSubmitRequestDTO requestDTO) throws IOException, InterruptedException {
        CompileStrategy strategy = null;
        if(requestDTO.getLangCode().equals("JAVA_11")) {
            strategy = applicationContext.getBean(Java11CompileStrategy.class);
        }
        ConditionDTO conditionDTO = compileService.getCondition(requestDTO.getProblemNo(), requestDTO.getLangCode());
        compileService.compileAndRun(
                strategy,
                requestDTO.getSourceCode(),
                conditionDTO.getInput(),
                conditionDTO.getOutput(),
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
    }
}
