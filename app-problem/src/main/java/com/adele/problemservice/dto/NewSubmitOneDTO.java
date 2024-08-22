package com.adele.problemservice.dto;

import com.adele.problemservice.domain.ProgramLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewSubmitOneDTO {
    private List<ProgramLanguage> languageList;
}
