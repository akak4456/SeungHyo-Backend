package com.adele.domainproblem.dto;

import com.adele.domainproblem.SubmitStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubmitResponse {
    private boolean result;
    private SubmitStatus status;
}
