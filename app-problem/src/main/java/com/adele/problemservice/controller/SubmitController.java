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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        kafkaTemplate.send("submit." + submitNo, new KafkaCompile(
                CompileStatus.START_FOR_KAFKA,
                -1L,
                "",
                "",
                null,
                null
        ));
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
     *     <li><b>problemNo</b> 문제 번호</li>
     *     <li><b>problemTitle</b> 문제 제목</li>
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
        ProblemGradeResponse result = submitService.getKafkaCompiles(submitNo);
        return ApiResult.<ProblemGradeResponse>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("문제 채점 정보 얻기 성공")
                .data(result)
                .build();
    }

    /**
     * problem list 를 조회한다.
     * @param pageable
     * page: 조회할 page number. 0부터 시작한다
     * size: 한 페이지당 들어갈 content 갯수
     * @return Page&lt;ProblemListDTO&gt;
     * <ul>
     *     <li><b>totalElements<b/> 조회된 elements 수</li>
     *     <li><b>totalPages</b> 조회된 총 page 숫자</li>
     *     <li>
     *         <p>content(ReflectionNoteListDTO)</p>
     *         <ul>
     *             <li><b>submitNo</b> 제출 번호</li>
     *             <li><b>problemTitle</b> 문제 제목</li>
     *             <li><b>submitStatus</b> 제출 상태</li>
     *             <li><b>langName</b> 언어이름</li>
     *             <li><b>submitDate</b> 제출일자</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @GetMapping({""})
    public ApiResult<Page<ReflectionNoteListDTO>> getSearch(
            @PageableDefault
            Pageable pageable
    ) {
        Page<ReflectionNoteListDTO> page = submitService.searchReflectionNotePage(pageable);
        return ApiResult.<Page<ReflectionNoteListDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("리스트 조회 성공")
                .data(page)
                .build();
    }
}
