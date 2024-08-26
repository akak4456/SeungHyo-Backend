package com.adele.domainproblem.dto;

import com.adele.domainproblem.domain.ProgramLanguage;
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
