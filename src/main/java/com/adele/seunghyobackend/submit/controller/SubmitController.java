package com.adele.seunghyobackend.submit.controller;

import com.adele.seunghyobackend.ApiResult;
import com.adele.seunghyobackend.submit.dto.NewSubmitRequestDTO;
import com.adele.seunghyobackend.submit.dto.NewSubmitResultDTO;
import com.adele.seunghyobackend.submit.service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.adele.seunghyobackend.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1/submit")
@RequiredArgsConstructor
@Slf4j
public class SubmitController {
    private final SubmitService submitService;
    /**
     * 소스코드 새로운 제출을 시도한다
     * @param newSubmitRequestDTO
     * <ul>
     *     <li><b>problemNo</b> 해당하는 문제 번호</li>
     *     <li><b>langCode</b> 제출한 언어 코드</li>
     *     <li><b>sourceCodeDisclosureScope</b> 소스코드 공개 범위 SourceCodeDisclosureScope class 의 enum value 를 따름 @see SourceCodeDisclosureScope</li>
     *     <li><b>sourceCode</b> 소스 코드</li>
     * </ul>
     * @return NewSubmitResultDTO
     * 제출 시도 성공했는지 여부
     */
    @PostMapping("")
    public ApiResult<NewSubmitResultDTO> newSubmit(@RequestBody NewSubmitRequestDTO newSubmitRequestDTO) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String memberId = ((User)authentication.getPrincipal()).getUsername();
        submitService.tryNewSubmit(memberId, newSubmitRequestDTO);
        return ApiResult.<NewSubmitResultDTO>builder()
                .code(CODE_SUCCESS)
                .message("제출 시도 성공")
                .build();
    }
}
