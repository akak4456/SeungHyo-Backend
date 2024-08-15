package com.adele.problemservice.domain;

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
@SequenceGenerator(name="seq_problem_tag", sequenceName = "seq_problem_tag", initialValue = 1, allocationSize = 1)
public class ProblemTag {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_problem_tag")
    @Column(name = "tag_no")
    private Long tagNo;

    private String tagName;

    @Column(name="background_color")
    private String backgroundColor;
}
