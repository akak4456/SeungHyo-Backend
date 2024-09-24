package com.adele.domainboard.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_like")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "boardLikeNo")
@DynamicInsert
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_like_no")
    private Long boardLikeNo;

    @ManyToOne
    @JoinColumn(name = "board_no")
    private Board board;

    @Column(name = "member_id")
    private String memberId;
}
