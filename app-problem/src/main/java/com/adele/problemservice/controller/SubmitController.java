package com.adele.problemservice.controller;

import com.adele.domainproblem.dto.ProblemGradeResponse;
import com.adele.domainproblem.dto.ReflectionNoteListDTO;
import com.adele.domainproblem.dto.SubmitStatisticsResponse;
import com.adele.domainproblem.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submit")
@RequiredArgsConstructor
@Slf4j
public class SubmitController {
    private final SubmitService submitService;
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
    public ProblemGradeResponse getProblemGrade(@PathVariable("submitNo") Long submitNo) {
        return submitService.getKafkaCompiles(submitNo);
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
    public Page<ReflectionNoteListDTO> getSearch(
            @PageableDefault
            Pageable pageable
    ) {
        return submitService.searchReflectionNotePage(pageable);
    }

    /**
     * user 화면에 보이는 통계 정보들을 구성하기 위해 호출함
     * @param memberId 통계 조회할 유저 id
     * @return SubmitStatisticsResponse doc 주석 참조
     */
    @GetMapping("/statistics")
    public SubmitStatisticsResponse getSubmitStatistics(@RequestParam("memberId") String memberId) {
        return submitService.getSubmitStatistics(memberId);
    }
}
