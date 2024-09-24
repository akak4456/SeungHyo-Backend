package com.adele.domainboard.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "reply_like")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "replyLikeNo")
@DynamicInsert
public class ReplyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_like_no")
    private Long replyLikeNo;

    @ManyToOne
    @JoinColumn(name = "reply_no")
    private Reply reply;

    @Column(name = "member_id")
    private String memberId;
}
