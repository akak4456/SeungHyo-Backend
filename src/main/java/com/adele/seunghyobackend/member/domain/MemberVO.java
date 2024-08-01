package com.adele.seunghyobackend.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "MEMBER")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MemberVO {
    @Id
    @Column(name="USER_ID")
    private String userId;

    @Column(name="USER_PW")
    private String userPw;

    @Column(name="STATUS_MESSAGE")
    private String statusMessage;

    @Column(name="DELETE_YN")
    private String deleteYn; // TODO 나중에 boolean 으로 바꾸는 게 적절할 듯

    @Column(name="EMAIL")
    private String email;

}
