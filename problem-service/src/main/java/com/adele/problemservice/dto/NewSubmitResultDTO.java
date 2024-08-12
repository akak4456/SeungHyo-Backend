package com.adele.problemservice.dto;

import com.adele.problemservice.domain.SubmitList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewSubmitResultDTO {
    private boolean result;
    @JsonIgnore
    private SubmitList submit;
}
