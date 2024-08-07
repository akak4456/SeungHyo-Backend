package com.adele.seunghyobackend.submit.service;

import com.adele.seunghyobackend.submit.dto.NewSubmitRequestDTO;
import com.adele.seunghyobackend.submit.dto.NewSubmitResultDTO;

public interface SubmitService {
    /**
     * 새로운 제출 시도 Service
     * @param newSubmitRequestDTO service request
     * @return NewSubmitResultDTO service response
     */
    NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO);
}
