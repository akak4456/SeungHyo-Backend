package com.adele.boardservice.repository;

import com.adele.boardservice.dto.ProblemDTO;
import com.adele.common.ApiResult;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "problem-service")
public interface ProblemClient {
    @GetMapping("api/v1/problem/{problemNo}")
    ResponseEntity<ApiResult<ProblemDTO>> getProblemOne(@PathVariable("problemNo") String problemNo);
}
