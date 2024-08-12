package com.adele.problemservice.dto;

import com.adele.problemservice.SubmitStatus;
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
