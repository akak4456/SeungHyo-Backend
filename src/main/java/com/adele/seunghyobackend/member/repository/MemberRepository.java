package com.adele.seunghyobackend.member.repository;

import com.adele.seunghyobackend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
}
