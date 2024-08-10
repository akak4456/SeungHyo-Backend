package com.adele.boardservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reply")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "replyNo")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_no")
    private Long replyNo;

    @ManyToOne
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(name = "member_id")
    private String memberId;

    @Column(name="like_count")
    private Long likeCount;

    @Column(name="reply_content")
    private String replyContent;

    @Column(name="source_code")
    private String sourceCode;
}
