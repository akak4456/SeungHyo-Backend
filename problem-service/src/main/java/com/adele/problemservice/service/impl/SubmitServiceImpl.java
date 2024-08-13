package com.adele.problemservice.service.impl;

import com.adele.problemservice.CompileStatus;
import com.adele.problemservice.SourceCodeDisclosureScope;
import com.adele.problemservice.SubmitStatus;
import com.adele.problemservice.domain.Problem;
import com.adele.problemservice.domain.ProblemGrade;
import com.adele.problemservice.domain.ProgramLanguage;
import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.*;
import com.adele.problemservice.repository.ProblemGradeRepository;
import com.adele.problemservice.repository.ProblemRepository;
import com.adele.problemservice.repository.ProgramLanguageRepository;
import com.adele.problemservice.repository.SubmitRepository;
import com.adele.problemservice.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmitServiceImpl implements SubmitService {
    private final SubmitRepository submitRepository;
    private final ProblemRepository problemRepository;
    private final ProgramLanguageRepository programLanguageRepository;
    private final ProblemGradeRepository problemGradeRepository;
    private final JdbcTemplate jdbcTemplate;
    @Override
    public NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO) {
        Problem problem = problemRepository.getReferenceById(newSubmitRequestDTO.getProblemNo());
        ProgramLanguage language = programLanguageRepository.getReferenceById(newSubmitRequestDTO.getLangCode());
        SubmitList submit = SubmitList.builder()
                .memberId(memberId)
                .problem(problem)
                .submitResult(SubmitStatus.WAIT)
                .maxMemory(BigDecimal.ZERO)
                .maxTime(BigDecimal.ZERO)
                .language(language)
                .openRange(SourceCodeDisclosureScope.valueOf(newSubmitRequestDTO.getSourceCodeDisclosureScope()))
                .sourceCode(newSubmitRequestDTO.getSourceCode())
                .build();
        submitRepository.save(submit);
        return new NewSubmitResultDTO(true, submit);
    }

    @Override
    public UpdateSubmitResponse saveCompileResult(Long submitNo, List<CompileResultDTO> compileResults) {
        SubmitStatus status = SubmitStatus.CORRECT;
        if(compileResults.stream().anyMatch((result) -> result.getStatus() == CompileStatus.COMPILE_ERROR)) {
            status = SubmitStatus.COMPILE_ERROR;
        }
        else if(compileResults.stream().anyMatch((result) -> result.getStatus() == CompileStatus.RUNTIME_ERROR)) {
            status = SubmitStatus.RUNTIME_ERROR;
        }
        else if(compileResults.stream().anyMatch((result) -> result.getStatus() == CompileStatus.WRONG)) {
            status = SubmitStatus.WRONG;
        }
        SubmitList originSubmit = submitRepository.findById(submitNo).orElse(null);
        if(originSubmit == null) {
            return new UpdateSubmitResponse(false, SubmitStatus.ETC_ERROR);
        }
        originSubmit.setSubmitResult(status);
        String sql = "INSERT INTO problem_grade(grade_result, input_no, output_no, submit_no, grade_case_no, compile_error_reason, runtime_error_reason) VALUES(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CompileResultDTO compileResult = compileResults.get(i);
                ps.setString(1, compileResult.getStatus().toString());
                if(compileResult.getExpectedInput() == null) {
                    ps.setLong(2, -1L);
                } else {
                    ps.setLong(2, compileResult.getExpectedInput().getInputNo());
                }
                if(compileResult.getExpectedOutput() == null) {
                    ps.setLong(3, -1L);
                } else {
                    ps.setLong(3, compileResult.getExpectedOutput().getOutputNo());
                }
                ps.setLong(4, submitNo);
                ps.setInt(5, i + 1);
                if(compileResult.getCompileErrorReason() != null) {
                    ps.setString(6, compileResult.getCompileErrorReason().toString());
                } else {
                    ps.setString(6, null);
                }
                if(compileResult.getRuntimeErrorReason() != null) {
                    ps.setString(7, compileResult.getRuntimeErrorReason().toString());
                } else {
                    ps.setString(7, null);
                }
            }

            @Override
            public int getBatchSize() {
                return compileResults.size();
            }
        });
        return new UpdateSubmitResponse(true, status);
    }

    @Override
    public UpdateSubmitResponse updateSubmitStatusWhenError(Long submitNo) {
        SubmitList originSubmit = submitRepository.findById(submitNo).orElse(null);
        if(originSubmit == null) {
            return new UpdateSubmitResponse(false, SubmitStatus.ETC_ERROR);
        }
        originSubmit.setSubmitResult(SubmitStatus.ETC_ERROR);
        return new UpdateSubmitResponse(true, SubmitStatus.ETC_ERROR);
    }

    @Override
    public List<KafkaCompile> getKafkaCompiles(Long submitNo) {
        return problemGradeRepository.findBySubmitNo(submitNo);
    }
}
