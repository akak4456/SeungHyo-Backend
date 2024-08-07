package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "problem_tag")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "tagName")
public class ProblemTag {
    @Id
    @Column(name="tag_name")
    private String tagName;

    @Column(name="background_color")
    private String backgroundColor;
}
