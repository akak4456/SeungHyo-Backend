package com.adele.appsubmit.controller;

import com.adele.appsubmit.executor.CompileExecutor;
import com.adele.appsubmit.compilestrategy.CompileStrategy;
import com.adele.appsubmit.compilestrategy.impl.Java11CompileStrategy;
import com.adele.appsubmit.kafka.KafkaDynamicListener;
import com.adele.appsubmit.properties.CompilerConfigProperties;
import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.dto.ConditionDTO;
import com.adele.domainproblem.dto.KafkaCompile;
import com.adele.domainproblem.dto.NewSubmitRequestDTO;
import com.adele.domainproblem.dto.NewSubmitResultDTO;
import com.adele.domainproblem.service.ProblemService;
import com.adele.domainproblem.service.SubmitService;
import com.adele.internalcommon.request.AuthHeaderConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/compile")
@RequiredArgsConstructor
@Slf4j
public class CompileController {
    private final SubmitService submitService;
    private final ProblemService problemService;
    private final KafkaTemplate<String, KafkaCompile> kafkaTemplate;
    private final CompileExecutor compileExecutor;
    private final CompilerConfigProperties compilerConfigProperties;
    private final KafkaDynamicListener kafkaDynamicListener;
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
    public NewSubmitResultDTO newSubmit(@RequestHeader(AuthHeaderConstant.AUTH_USER) String memberId, @RequestBody @Valid NewSubmitRequestDTO newSubmitRequestDTO) throws IOException, InterruptedException {
        NewSubmitResultDTO result = submitService.tryNewSubmit(memberId, newSubmitRequestDTO);
        tryCompile(result.getSubmitNo(), newSubmitRequestDTO);
        return result;
    }

    private void tryCompile(Long submitNo, NewSubmitRequestDTO requestDTO) throws IOException, InterruptedException {
        CompileStrategy strategy = null;
        if(requestDTO.getLangCode().equals("JAVA_11")) {
            strategy = new Java11CompileStrategy(compilerConfigProperties);
        }
        ConditionDTO conditionDTO = problemService.getCondition(requestDTO.getProblemNo(), requestDTO.getLangCode());
        kafkaTemplate.send("submit." + submitNo, new KafkaCompile(
                CompileStatus.START_FOR_KAFKA,
                -1L,
                "",
                "",
                null,
                null
        ));
        compileExecutor.compileAndRun(
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
}
