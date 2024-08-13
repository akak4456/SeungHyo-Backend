package com.adele.problemservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.AuthHeaderConstant;
import com.adele.common.ResponseCode;
import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.compilestrategy.CompileStrategy;
import com.adele.problemservice.compilestrategy.impl.Java11CompileStrategy;
import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.*;
import com.adele.problemservice.service.CompileService;
import com.adele.problemservice.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/submit")
@RequiredArgsConstructor
@Slf4j
public class SubmitController {
    private final SubmitService submitService;

    private final CompileService compileService;

    private final KafkaTemplate<String, KafkaCompile> kafkaTemplate;

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
        tryCompile(result.getSubmitNo(), newSubmitRequestDTO);
        return ApiResult.<NewSubmitResultDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("제출 시도 성공")
                .data(result)
                .build();
    }

    private void tryCompile(Long submitNo, NewSubmitRequestDTO requestDTO) throws IOException, InterruptedException {
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
                    log.info("submitNo: {}, idx : {}, result : {}", submitNo, idx, compileResult);
                    kafkaTemplate.send("submit." + submitNo, new KafkaCompile(
                            compileResult.getStatus(),
                            (long)idx + 1,
                            compileResult.getExpectedInput().getInputSource(),
                            compileResult.getExpectedOutput().getOutputSource(),
                            compileResult.getCompileErrorReason(),
                            compileResult.getRuntimeErrorReason()
                    ));
                }).thenAccept(result -> {
                    submitService.saveCompileResult(submitNo, result);
                    kafkaTemplate.send("submit." + submitNo, new KafkaCompile(
                            CompileStatus.EXIT_FOR_KAFKA,
                            -1L,
                            "",
                            "",
                            null,
                            null
                    ));
        }).exceptionally(ex -> {
            // 예외 처리
            log.error("An error occurred during compilation: ", ex);
            submitService.updateSubmitStatusWhenError(submitNo);
            kafkaTemplate.send("submit." + submitNo, new KafkaCompile(
                    CompileStatus.EXIT_FOR_KAFKA,
                    -1L,
                    "",
                    "",
                    null,
                    null
            ));
            throw new RuntimeException(ex);
        });;
    }

    /**
     * 제출번호에 해당하는 문제 채점 결과를 얻어온다
     * @param submitNo 제출번호
     * @return ProblemGradeResponse 문제 채점 결과들
     * <ul>
     *     <li><b>compileStatus</b> 컴파일 결과</li>
     *     <li><b>caseNo</b> 케이스 순번</li>
     *     <li><b>inputSource</b> 케이스 입력</li>
     *     <li><b>outputSource</b> 케이스 출력</li>
     *     <li><b>compileErrorReason</b> 컴파일 오류 이유</li>
     *     <li><b>runtimeErrorReason</b> 런타임 오류 이유</li>
     * </ul>
     */
    @GetMapping("{submitNo}")
    public ApiResult<ProblemGradeResponse> getProblemGrade(@PathVariable("submitNo") Long submitNo) {
        List<KafkaCompile> kafkaCompiles = submitService.getKafkaCompiles(submitNo);
        return ApiResult.<ProblemGradeResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("제출 시도 성공")
                .data(new ProblemGradeResponse(kafkaCompiles))
                .build();
    }
}
