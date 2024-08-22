package com.adele.problemservice.controller;

import com.adele.common.ApiResult;
import com.adele.common.ResponseCode;
import com.adele.problemservice.dto.ProblemListDTO;
import com.adele.problemservice.dto.ProblemOneDTO;
import com.adele.problemservice.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/problem")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {
    private final ProblemService problemService;

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
     *         <p>content(ProblemListDTO)</p>
     *         <ul>
     *             <li><b>problemNo</b> 문제 번호</li>
     *             <li><b>problemTitle</b> 문제 제목</li>
     *             <li><b>correctPeopleCount</b> 맞힌 사람 수</li>
     *             <li><b>submitCount</b> 제출 수</li>
     *             <li><b>correctRatio</b> 정답 비율</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @GetMapping({""})
    public ApiResult<Page<ProblemListDTO>> getSearch(
            @PageableDefault
            Pageable pageable
    ) {
        Page<ProblemListDTO> page = problemService.searchPage(pageable);
        return ApiResult.<Page<ProblemListDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("리스트 조회 성공")
                .data(page)
                .build();
    }

    /**
     * 하나를 조회한다
     * @param problemNo 조회할 문제 번호
     * @return ProblemOneDTO
     * <ul>
     *     <li><b>problemTitle</b> 문제 제목</li>
     *     <li><b>problemTags</b> 문제 태그들</li>
     *     <li><b>problemCondition</b> 문제 조건들</li>
     *     <li><b>correctRatio</b> 정답 비율</li>
     *     <li><b>problemExplain</b> 문제에 대한 정보</li>
     *     <li><b>problemInputExplain</b> 입력 설명</li>
     *     <li><b>problemOutputExplain</b> 출력 설명</li>
     *     <li><b>problemInput</b> 예제 입력들</li>
     *     <li><b>problemOutput</b> 예제 출력들</li>
     *     <li><b>algorithmCategory</b> 알고리즘 분류</li>
     *     <li><b>programLanguages</b> 제출 지원 언어</li>
     * </ul>
     */
    @GetMapping("{problemNo}")
    public ApiResult<ProblemOneDTO> getOne(@PathVariable Long problemNo) {
        ProblemOneDTO one = problemService.problemOne(problemNo);
        return ApiResult.<ProblemOneDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message("하나 조회 성공")
                .data(one)
                .build();
    }
}
