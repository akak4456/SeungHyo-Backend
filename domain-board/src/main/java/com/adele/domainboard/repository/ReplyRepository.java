package com.adele.domainboard.repository;

import com.adele.domainboard.domain.Reply;
import com.adele.domainboard.repository.custom.ReplyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom {
}
