package com.adele.seunghyobackend.member.repository;

import com.adele.seunghyobackend.member.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
}
