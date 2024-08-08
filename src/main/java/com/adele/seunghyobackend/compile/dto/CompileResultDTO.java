package com.adele.seunghyobackend.compile.dto;

import com.adele.seunghyobackend.compile.CompileStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CompileResultDTO {
    private CompileStatus status;
    private String output;
    private String error;
}
