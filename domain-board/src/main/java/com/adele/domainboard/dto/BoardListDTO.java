package com.adele.domainboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardListDTO {
    private Long boardNo;
    private String boardTitle;
    private String categoryCode;
    private String categoryName;
    private String langName;
    private String memberId;
    private Long replyCount;
    private Long likeCount;
    private LocalDateTime regDate;
}
