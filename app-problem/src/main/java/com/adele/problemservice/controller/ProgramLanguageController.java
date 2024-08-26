package com.adele.problemservice.controller;

import com.adele.domainproblem.dto.NewSubmitOneDTO;
import com.adele.domainproblem.service.ProgramLanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/programlanguage")
@RequiredArgsConstructor
@Slf4j
public class ProgramLanguageController {
    private final ProgramLanguageService programLanguageService;
    /**
     * 제출하기 화면에 진입할 때 필요한 데이터를 보내준다
     * @param problemNo 제출할 문제 번호
     * @return NewSubmitOneDTO
     * <ul>
     *     <li><b>languageList</b> 문제가 지원하는 언어들</li>
     * </ul>
     */
    @GetMapping({"{problemNo}"})
    public NewSubmitOneDTO addGetOne(
            @PathVariable Long problemNo
    ) {
        return programLanguageService.findAll(problemNo);
    }

    /**
     * 게시판 화면에 진입할 때 필요한 데이터를 보내준다
     * @return NewSubmitOneDTO
     * <ul>
     *     <li><b>languageList</b> 지원하는 모든 언어들</li>
     * </ul>
     */
    @GetMapping("")
    public NewSubmitOneDTO getAllLanguages() {
        return programLanguageService.findAll();
    }
}
