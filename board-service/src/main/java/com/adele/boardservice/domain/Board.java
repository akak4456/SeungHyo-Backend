package com.adele.boardservice.domain;

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

    @Column(name = "member_id")
    private String memberId;

    @Column(name="board_title")
    private String boardTitle;

    @ManyToOne
    @JoinColumn(name="category_code")
    private BoardCategory boardCategory;

    @Column(name="lang_code")
    private String langCode;

    /**
     * 원래는 program_language 랑 lang_code 로 join 을 해야 한다. 하지만
     * lang_code 에 따른 lang_name 이 변할 일이 많지 않으므로
     * 중복 데이터를 담도록 한다.
     */
    @Column(name="lang_name")
    private String langName;

    @Column(name="like_count")
    private Long likeCount;

    @Column(name="reg_date")
    private LocalDateTime regDate;

    @Column(name = "problem_no")
    private Long problemNo;

    @Column(name="board_content")
    private String boardContent;

    @Column(name="source_code")
    private String sourceCode;
}
