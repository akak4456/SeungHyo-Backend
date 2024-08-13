package com.adele.problemservice.repository.custom;

import com.adele.problemservice.dto.ReflectionNoteListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmitRepositoryCustom {
    Page<ReflectionNoteListDTO> searchPage(Pageable pageable);
}
