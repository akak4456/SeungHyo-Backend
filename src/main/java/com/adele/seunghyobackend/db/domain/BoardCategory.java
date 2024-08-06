package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "board_category")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "categoryCode")
public class BoardCategory {
    @Id
    @Column(name="category_code")
    private String categoryCode;

    @Column(name="category_name")
    private String categoryName;
}
