package com.adele.seunghyobackend.compile.dto;

import com.adele.seunghyobackend.compile.CompileStatus;
import lombok.*;

import java.util.List;

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
