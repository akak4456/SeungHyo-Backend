package com.adele.boardservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "replyNo")
@SequenceGenerator(name = "seq_reply", sequenceName = "seq_reply", initialValue = 1, allocationSize = 1)
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reply")
    @Column(name = "reply_no")
    private Long replyNo;

    @ManyToOne
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(name = "member_id")
    private String memberId;

    @Column(name="like_count")
    private Long likeCount;

    @Lob
    @Column(name="reply_content")
    private String replyContent;

    @Lob
    @Column(name="source_code")
    private String sourceCode;

    @Column(name="reg_date")
    private LocalDateTime regDate;
}
