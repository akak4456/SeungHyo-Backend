package com.adele.problemservice.dto;

import com.adele.problemservice.SubmitStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReflectionNoteListDTO {
    private Long submitNo;
    private String problemTitle;
    private SubmitStatus submitResult;
    private String langName;
    private LocalDateTime submitDate;
}
