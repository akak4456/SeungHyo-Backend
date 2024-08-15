package com.adele.problemservice.domain;

import com.adele.problemservice.SourceCodeDisclosureScope;
import com.adele.problemservice.SubmitStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
@Builder
@SequenceGenerator(name = "seq_submt_list", sequenceName = "seq_submt_list", initialValue = 1, allocationSize = 1)
public class SubmitList {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_submt_list")
    @Column(name = "submit_no")
    private Long submitNo;

    @Column(name = "member_id")
    private String memberId;

    @ManyToOne
    @JoinColumn(name = "problem_no")
    @JsonIgnore
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

    @CreationTimestamp
    @Column(name="submit_date")
    private LocalDateTime submitDate;

    @Enumerated(EnumType.STRING)
    @Column(name="open_range")
    private SourceCodeDisclosureScope openRange;

    @Lob
    @Column(name="source_code")
    private String sourceCode;
}
