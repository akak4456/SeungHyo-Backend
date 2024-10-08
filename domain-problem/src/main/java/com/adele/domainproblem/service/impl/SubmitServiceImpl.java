package com.adele.domainproblem.service.impl;

import com.adele.domainproblem.CompileStatus;
import com.adele.domainproblem.SourceCodeDisclosureScope;
import com.adele.domainproblem.SubmitStatus;
import com.adele.domainproblem.domain.Problem;
import com.adele.domainproblem.domain.ProblemGrade;
import com.adele.domainproblem.domain.ProgramLanguage;
import com.adele.domainproblem.domain.SubmitList;
import com.adele.domainproblem.dto.*;
import com.adele.domainproblem.repository.ProblemGradeRepository;
import com.adele.domainproblem.repository.ProblemRepository;
import com.adele.domainproblem.repository.ProgramLanguageRepository;
import com.adele.domainproblem.repository.SubmitRepository;
import com.adele.domainproblem.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
        return new NewSubmitResultDTO(true, submit.getSubmitNo());
    }

    @Override
    public boolean saveCompileResult(Long submitNo, int gradeCaseNo, CompileResultDTO compileResult) {
        String sql = "INSERT INTO problem_grade(grade_result, input_no, output_no, submit_no, grade_case_no, compile_error_reason, runtime_error_reason) VALUES(?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int notUsed) throws SQLException {
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
                ps.setInt(5, gradeCaseNo + 1);
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
                return 1;
            }
        });
        return true;
    }

    @Override
    public UpdateSubmitResponse updateSubmitStatusWhenNormal(Long submitNo, List<CompileResultDTO> compileResults) {
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
    public ProblemGradeResponse getKafkaCompiles(Long submitNo) {
        ProblemGradeResponse response = new ProblemGradeResponse();
        SubmitList submit = submitRepository.findById(submitNo).orElse(null);
        if(submit != null) {
            response.setKafkaCompiles(problemGradeRepository.findBySubmitNo(submitNo));
            Problem problem = submit.getProblem();
            response.setProblemTitle(problem.getProblemTitle());
            response.setProblemNo(problem.getProblemNo());
        }
        return response;
    }

    @Override
    public Page<ReflectionNoteListDTO> searchReflectionNotePage(Pageable pageable) {
        return submitRepository.searchPage(pageable);
    }

    @Override
    public SubmitStatisticsResponse getSubmitStatistics(String memberId) {
        return submitRepository.getSubmitStatistics(memberId);
    }
}
