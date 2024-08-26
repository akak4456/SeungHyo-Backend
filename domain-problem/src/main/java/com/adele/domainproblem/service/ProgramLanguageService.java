package com.adele.domainproblem.service;


import com.adele.domainproblem.dto.NewSubmitOneDTO;

public interface ProgramLanguageService {
    /**
     * 문제가 지원하는 언어들 얻는 Service
     * @param problemNo 문제 번호
     * @return NewSubmitOneDTO 문제가 지원하는 언어들
     */
    NewSubmitOneDTO findAll(Long problemNo);

    /**
     * 지원하는 모든 언어들 얻는 Service
     * @return NewSubmitOneDTO 문제가 지원하는 언어들
     */
    NewSubmitOneDTO findAll();
}
