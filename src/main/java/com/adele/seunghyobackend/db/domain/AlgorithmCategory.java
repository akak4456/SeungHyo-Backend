package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "algorithm_category")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "algorithm_code")
public class AlgorithmCategory {
    @Id
    @Column(name = "algorithm_code")
    private String algorithmCode;

    @Column(name = "algorithm_name")
    private String algorithmName;
}
