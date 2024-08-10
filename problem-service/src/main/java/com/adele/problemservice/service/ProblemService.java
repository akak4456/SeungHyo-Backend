package com.adele.problemservice.service;

import com.adele.problemservice.dto.ProblemListDTO;
import com.adele.problemservice.dto.ProblemOneDTO;
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
}
