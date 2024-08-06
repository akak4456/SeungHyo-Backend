package com.adele.seunghyobackend.my.controller;

import com.adele.seunghyobackend.common.ApiResult;
import com.adele.seunghyobackend.my.dto.InfoEditResultDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditDTO;
import com.adele.seunghyobackend.my.dto.PatchInfoEditResultDTO;
import com.adele.seunghyobackend.my.service.MyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static com.adele.seunghyobackend.common.Constant.CODE_SUCCESS;

@RestController
@RequestMapping("/api/v1/my")
@RequiredArgsConstructor
@Slf4j
public class MyController {
    private final MyService myService;

    /**
     * 정보 수정에서 이용할 데이터를 조회한다
     * @return InfoEditResultDTO
     * <ul>
     *     <li><b>memberId</b> 조회한 member id</li>
     *     <li><b>statusMessage</b> 조회한 상태 메시지</li>
     *     <li><b>email</b> 조회한 이메일</li>
     * </ul>
     */
    @GetMapping("/info-edit")
    public ApiResult<InfoEditResultDTO> getInfoEdit() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String memberId = ((User)authentication.getPrincipal()).getUsername();
        InfoEditResultDTO result = myService.getInfoEdit(memberId);
        return ApiResult.<InfoEditResultDTO>builder()
                .code(CODE_SUCCESS)
                .message("info edit 정보 조회 성공")
                .data(result)
                .build();
    }

    /**
     * 정보 수정을 시도한다
     * @param dto
     * <ul>
     *     <li><b>memberId</b> 정보 수정할 id</li>
     *     <li><b>memberPw</b> 정보 수정할 pw. pw 는 정보 수정할 때 체크하는 용도로 쓰인다</li>
     *     <li><b>statusMessage</b> 정보 수정할 상태 메시지</li>
     *     <li><b>email</b> 정보 수정할 email</li>
     * </ul>
     * @return PatchInfoEditResultDTO
     * <ul>
     *     <li><b>idNotMatch</b> 입력한 id 랑 실제 아이디랑 다른지 여부</li>
     *     <li><b>idNotValidForm</b> id form 이 올바르지 않은지 여부</li>
     *     <li><b>statusMessageNotValidForm</b> status message form 이 올바르지 않은지 여부</li>
     *     <li><b>pwNotValidForm</b> 비밀번호가 올바르지 않은지 여부</li>
     *     <li><b>emailNotValidForm</b> 이메일이 올바르지 않은지 여부</li>
     *     <li><b>pwNotMatch</b> 입력한 pw 랑 실제 pw 랑 다른지 여부</li>
     * </ul>
     */
    @PatchMapping("/info-edit")
    public ApiResult<PatchInfoEditResultDTO> patchInfoEdit(@RequestBody PatchInfoEditDTO dto) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String memberId = ((User)authentication.getPrincipal()).getUsername();
        PatchInfoEditResultDTO result = myService.patchInfoEdit(dto, memberId.equals(dto.getMemberId()));
        return ApiResult.<PatchInfoEditResultDTO>builder()
                .code(CODE_SUCCESS)
                .message("info edit 정보 수정 시도 성공")
                .data(result)
                .build();
    }
}
