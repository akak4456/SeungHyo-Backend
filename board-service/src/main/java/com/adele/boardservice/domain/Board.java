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
@SequenceGenerator(name = "seq_board", sequenceName = "seq_board", initialValue = 1, allocationSize = 1)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_board")
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

    /**
     * 원래는 program 이랑 problem_no 로 join 을 해야 한다. 하지만
     * problem_no 에 따른 problem_title 이 변할 일이 많지 않으므로
     * 중복 데이터를 담도록 한다.
     */
    @Column(name="problem_title")
    private String problemTitle;

    @Lob
    @Column(name="board_content")
    private String boardContent;

    @Lob
    @Column(name="source_code")
    private String sourceCode;
}
