package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "boardNo")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long boardNo;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name="board_title")
    private String boardTitle;

    @ManyToOne
    @JoinColumn(name="category_code")
    private BoardCategory boardCategory;

    @ManyToOne
    @JoinColumn(name="lang_code")
    private ProgramLanguage programLanguage;

    @Column(name="like_count")
    private Long likeCount;

    @Column(name="reg_date")
    private LocalDateTime regDate;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Column(name="board_content")
    private String boardContent;

    @Column(name="source_code")
    private String sourceCode;
}
