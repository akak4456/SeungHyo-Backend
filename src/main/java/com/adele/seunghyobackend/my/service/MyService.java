package com.adele.seunghyobackend.my.service;

import com.adele.seunghyobackend.my.dto.*;

public interface MyService {
    /**
     * 회원정보 조회 service
     * @param memberId 조회할 아이디
     * @return InfoEditResultDTO 조회 결과
     */
    InfoEditResultDTO getInfoEdit(String memberId);

    /**
     * 회원정보 수정 service
     * @param dto 수정할 데이터
     * @return PatchInfoEditResultDTO 수정 시도 결과
     */
    PatchInfoEditResultDTO patchInfoEdit(PatchInfoEditDTO dto, boolean idMatch);

    ChangePwResultDTO tryChangePw(String memberId, ChangePwDTO dto);

    /**
     * 회원탈퇴 service
     * @param memberId 회원탈퇴할 id
     * @return 회원탈퇴 성공 여부
     */
    boolean withdraw(String memberId);
}
