package com.adele.domainproblem.service;

import com.adele.domainproblem.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmitService {
    /**
     * 새로운 제출 시도 Service
     * @param newSubmitRequestDTO service request
     * @return NewSubmitResultDTO service response
     */
    NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO);

    /**
     * 컴파일 서비스에서 문제가 없을 시 컴파일 결과들을 저장하는
     * service
     *
     * @param submitNo       submit 번호
     * @param gradeCaseNo grade case no
     * @param compileResult compile 결과
     * @return UpdateSubmitResponse response
     */
    boolean saveCompileResult(Long submitNo, int gradeCaseNo, CompileResultDTO compileResult);

    /**
     * 컴파일 서비스에 문제가 없을 때 status 를 바꿔주는 service
     * @param submitNo submit 번호
     * @param compileResults ompile 결과들 input의 개수와 일치할 수도 일치하지 않을 수도 있다.
     * @return UpdateSubmitResponse response
     */
    UpdateSubmitResponse updateSubmitStatusWhenNormal(Long submitNo, List<CompileResultDTO> compileResults);

    /**
     * 컴파일 서비스에서 문제가가 발생할 때 status를 바꿔주는
     * service
     *
     * @param submitNo submit 번호
     * @return UpdateSubmitResponse response
     */
    UpdateSubmitResponse updateSubmitStatusWhenError(Long submitNo);

    /**
     * 제출번호에 해당하는 채점 결과를 리턴한다.
     * @param submitNo 제출번호
     * @return ProblemGradeResponse 채점 결과
     */
    ProblemGradeResponse getKafkaCompiles(Long submitNo);

    /**
     * 오답노트 list 조회 서비스
     * @param pageable 조회할 페이지
     * @return Page&lt;ProblemListDTO&gt; 페이지 객체
     */
    Page<ReflectionNoteListDTO> searchReflectionNotePage(Pageable pageable);
}
