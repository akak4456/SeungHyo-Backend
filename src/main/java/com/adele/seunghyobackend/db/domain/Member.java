package com.adele.seunghyobackend.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "memberId")
public class Member implements UserDetails {
    @Id
    @Column(name="MEMBER_ID")
    private String memberId;

    @JsonIgnore
    @Column(name="MEMBER_PW")
    private String memberPw;

    @JsonIgnore
    @Column(name="STATUS_MESSAGE")
    private String statusMessage;

    @JsonIgnore
    @Column(name="DELETE_YN")
    private String deleteYn; // TODO 나중에 boolean 으로 바꾸는 게 적절할 듯 그리고 DEFAULT 로 Y로 부여하기

    @JsonIgnore
    @Column(name="EMAIL")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "MEMBER_ROLES", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "ROLES")
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return memberPw;
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
