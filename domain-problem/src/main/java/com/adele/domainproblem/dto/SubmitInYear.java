package com.adele.domainproblem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitInYear {
    /**
     * 년
     */
    private Integer year;
    /**
     * 월
     */
    private Integer month;
    /**
     * 맞은 비율
     */
    private Double ratio;
}