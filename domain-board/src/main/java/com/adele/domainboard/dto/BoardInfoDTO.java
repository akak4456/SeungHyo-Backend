package com.adele.domainboard.dto;

import com.adele.domainboard.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardInfoDTO {
    private List<BoardInfoInMain> newBoard;// 새로운 글
    private List<BoardInfoInMain> popularBoard;// 인기글
    private List<BoardInfoInMain> noticeBoard; // 공지사항
}
