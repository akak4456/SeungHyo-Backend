package com.adele.boardservice.repository;

import com.adele.boardservice.domain.Reply;
import com.adele.boardservice.repository.custom.ReplyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom {
}
