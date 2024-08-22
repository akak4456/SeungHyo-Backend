package com.adele.boardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryDTO {
    private String categoryCode;
    private String categoryName;
}
