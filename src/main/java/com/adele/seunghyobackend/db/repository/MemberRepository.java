package com.adele.seunghyobackend.db.repository;

import com.adele.seunghyobackend.db.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
}
