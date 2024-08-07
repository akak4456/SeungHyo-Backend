package com.adele.seunghyobackend.problem.controller;

import com.adele.seunghyobackend.ApiResult;
import com.adele.seunghyobackend.problem.dto.ProblemListDTO;
import com.adele.seunghyobackend.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.adele.seunghyobackend.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1/problem")
@RequiredArgsConstructor
@Slf4j
public class ProblemListController {
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
        log.info("{}", pageable);
        Page<ProblemListDTO> page = problemService.searchPage(pageable);
        return ApiResult.<Page<ProblemListDTO>>builder()
                .code(CODE_SUCCESS)
                .message("리스트 조회 성공")
                .data(page)
                .build();
    }
}
