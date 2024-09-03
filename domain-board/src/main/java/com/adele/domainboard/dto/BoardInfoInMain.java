package com.adele.domainboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardInfoInMain {
    private Long boardNo;
    private String memberId;
    private String boardTitle;
    private LocalDateTime regDate;
    private Long likeCount;
    private Long replyCount;
}
