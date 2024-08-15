package com.adele.problemservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "algorithm_category")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "algorithmNo")
@SequenceGenerator(name = "seq_algorithm_category", sequenceName = "seq_algorithm_category", initialValue = 1, allocationSize = 1)
public class AlgorithmCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_algorithm_category")
    @Column(name = "algorithm_no")
    private Long algorithmNo;

    @Column(name = "algorithm_name")
    private String algorithmName;
}
