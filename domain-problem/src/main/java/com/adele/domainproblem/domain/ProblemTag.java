package com.adele.domainproblem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_tag")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "tagNo")
public class ProblemTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_no")
    private Long tagNo;

    private String tagName;

    @Column(name="background_color")
    private String backgroundColor;
}
