package com.adele.problemservice.service;

import com.adele.problemservice.dto.NewSubmitRequestDTO;
import com.adele.problemservice.dto.NewSubmitResultDTO;

public interface SubmitService {
    /**
     * 새로운 제출 시도 Service
     * @param newSubmitRequestDTO service request
     * @return NewSubmitResultDTO service response
     */
    NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO);
}
