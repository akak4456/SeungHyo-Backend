package com.adele.seunghyobackend.member.domain;

import com.adele.seunghyobackend.data.converter.BooleanToYNConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "memberId")
public class Member implements UserDetails {
    @Id
    @Column(name="member_id")
    private String memberId;

    @JsonIgnore
    @Column(name="member_pw")
    private String memberPw;

    @JsonIgnore
    @Column(name="status_message")
    private String statusMessage;

    @JsonIgnore
    @Column(name="delete_yn")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean deleteYn;

    @JsonIgnore
    @Column(name="email")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "roles")
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
