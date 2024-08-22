package com.adele.problemservice.repository;


import com.adele.problemservice.domain.SubmitList;
import com.adele.problemservice.dto.ReflectionNoteListDTO;
import com.adele.problemservice.repository.custom.SubmitRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitRepository extends JpaRepository<SubmitList, Long>, SubmitRepositoryCustom {
}
