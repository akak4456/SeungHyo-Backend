package com.adele.domainboard.repository;


import com.adele.domainboard.dto.ProblemDTO;
import com.adele.internalcommon.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "problem-service")
public interface ProblemClient {
    @GetMapping("api/v1/problem/{problemNo}")
    ResponseEntity<ApiResponse<ProblemDTO>> getProblemOne(@PathVariable("problemNo") String problemNo);
}
