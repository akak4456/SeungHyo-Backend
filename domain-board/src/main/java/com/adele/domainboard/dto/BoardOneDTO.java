package com.adele.domainboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardOneDTO {
    private String boardTitle;
    private Long problemNo;
    private String problemTitle;
    private String boardMemberId;
    private LocalDateTime boardRegDate;
    private Long boardLikeCount;
    private String boardContent;
    private String langCode;
    private String langName;
    private String sourceCode;
}
