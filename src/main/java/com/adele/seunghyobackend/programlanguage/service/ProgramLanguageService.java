package com.adele.seunghyobackend.programlanguage.service;

import com.adele.seunghyobackend.programlanguage.domain.ProgramLanguage;
import com.adele.seunghyobackend.programlanguage.dto.NewSubmitOneDTO;

import java.util.List;

public interface ProgramLanguageService {
    /**
     * 문제가 지원하는 언어들 얻는 Service
     * @param problemNo 문제 번호
     * @return NewSubmitOneDTO 문제가 지원하는 언어들
     */
    NewSubmitOneDTO findAll(Long problemNo);
}
