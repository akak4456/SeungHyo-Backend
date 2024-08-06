package com.adele.seunghyobackend.db.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Clob;
import java.time.LocalDateTime;

@Entity
@Table(name = "submit_list")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "submitNo")
public class SubmitList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submit_no")
    private Long submitNo;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    private Problem problem;

    @Column(name="submit_result")
    private String submitResult;

    @Column(name="max_memory")
    private BigDecimal maxMemory;

    @Column(name="max_time")
    private BigDecimal maxTime;

    @ManyToOne
    @JoinColumn(name = "lang_code")
    private ProgramLanguage language;

    @Column(name="submit_date")
    private LocalDateTime submitDate;

    @Column(name="open_range")
    private String openRange;

    @Column(name="source_code")
    private String sourceCode;
}
