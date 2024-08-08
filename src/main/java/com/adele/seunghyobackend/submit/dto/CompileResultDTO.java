package com.adele.seunghyobackend.submit.dto;

import com.adele.seunghyobackend.submit.CompileStatus;
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
