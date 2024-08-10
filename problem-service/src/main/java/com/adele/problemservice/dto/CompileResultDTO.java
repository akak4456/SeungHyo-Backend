package com.adele.problemservice.dto;

import com.adele.problemservice.CompileStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CompileResultDTO {
    private CompileStatus status;
    private String output;
    private Throwable error;
}
