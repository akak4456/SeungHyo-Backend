package com.adele.boardservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryResponse {
    private List<BoardCategoryDTO> boardCategory;
}
