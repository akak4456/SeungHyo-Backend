package com.adele.domainboard.domain;

import com.adele.domainboard.BooleanToYNConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name="is_for_admin")
    @ColumnDefault("N")
    @Convert(converter = BooleanToYNConverter.class)
    private boolean isForAdmin;

    @Column(name="order_num")
    private int orderNum;
}
