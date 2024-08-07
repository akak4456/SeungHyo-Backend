package com.adele.seunghyobackend.problem.domain;

import com.adele.seunghyobackend.data.SourceCodeDisclosureScope;
import com.adele.seunghyobackend.data.SubmitStatus;
import com.adele.seunghyobackend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Enumerated(EnumType.STRING)
    @Column(name="submit_result")
    private SubmitStatus submitResult;

    @Column(name="max_memory")
    private BigDecimal maxMemory;

    @Column(name="max_time")
    private BigDecimal maxTime;

    @ManyToOne
    @JoinColumn(name = "lang_code")
    private ProgramLanguage language;

    @Column(name="submit_date")
    private LocalDateTime submitDate;

    @Enumerated(EnumType.STRING)
    @Column(name="open_range")
    private SourceCodeDisclosureScope openRange;

    @Column(name="source_code")
    private String sourceCode;
}
