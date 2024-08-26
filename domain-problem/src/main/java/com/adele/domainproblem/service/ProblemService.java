package com.adele.domainproblem.service;

import com.adele.domainproblem.dto.ConditionDTO;
import com.adele.domainproblem.dto.ProblemListDTO;
import com.adele.domainproblem.dto.ProblemOneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemService {
    /**
     * problem list 조회 서비스
     * @param pageable 조회할 페이지
     * @return Page&lt;ProblemListDTO&gt; 페이지 객체
     */
    Page<ProblemListDTO> searchPage(Pageable pageable);

    /**
     * problem 상세 조회 서비스
     * @param problemNo problem 아이디 번호
     * @return ProblemOneDTO
     */
    ProblemOneDTO problemOne(Long problemNo);

    /**
     * input, output 을 얻는 service
     * @param problemNo 얻고자 하는 문제 번호, langCode 조건 언어 코드
     * @return ConditionDTO input, output, 조건들
     */
    ConditionDTO getCondition(Long problemNo, String langCode);
}
