package com.adele.domainboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReplyDTO {
    private Long replyNo;
    private String memberId;
    private LocalDateTime regDate;
    private Long likeCount;
    private String replyContent;
    private String langCode;
    private String langName;
    private String sourceCode;
}
