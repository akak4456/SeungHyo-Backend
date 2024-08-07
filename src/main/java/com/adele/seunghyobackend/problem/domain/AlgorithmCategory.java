package com.adele.seunghyobackend.problem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "algorithm_category")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "algorithmCode")
public class AlgorithmCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "algorithm_no")
    private Long algorithmNo;

    @Column(name = "algorithm_name")
    private String algorithmName;
}
