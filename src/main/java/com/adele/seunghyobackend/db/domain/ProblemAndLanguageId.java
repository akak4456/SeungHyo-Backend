package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProblemAndLanguageId implements Serializable {
    private Long problemNo;
    private String langCode;
}
