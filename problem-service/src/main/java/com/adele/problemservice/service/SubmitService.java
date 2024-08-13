package com.adele.problemservice.service;

import com.adele.problemservice.dto.*;

import java.util.List;

public interface SubmitService {
    /**
     * 새로운 제출 시도 Service
     * @param newSubmitRequestDTO service request
     * @return NewSubmitResultDTO service response
     */
    NewSubmitResultDTO tryNewSubmit(String memberId, NewSubmitRequestDTO newSubmitRequestDTO);

    /**
     * 컴파일 서비스에서 문제가 없을 시 컴파일 결과들을 저장하는
     * service
     *
     * @param submitNo       submit 번호
     * @param compileResults compile 결과들 input의 개수와 일치할 수도 일치하지 않을 수도 있다.
     * @return UpdateSubmitResponse response
     */
    UpdateSubmitResponse saveCompileResult(Long submitNo, List<CompileResultDTO> compileResults);

    /**
     * 컴파일 서비스에서 문제가가 발생할 때 status를 바꿔주는
     * service
     *
     * @param submitNo submit 번호
     * @return UpdateSubmitResponse response
     */
    UpdateSubmitResponse updateSubmitStatusWhenError(Long submitNo);

    /**
     * 제출번호에 해당하는 채점 결과를 리턴한다.
     * @param submitNo 제출번호
     * @return KafkaCompile 채점 결과
     */
    List<KafkaCompile> getKafkaCompiles(Long submitNo);
}
