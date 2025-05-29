package com.itwillbs.factron.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vacation_history")
public class VacationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 휴가 시작 날짜

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 휴가 종료 날짜

    @Column(name = "remark", length = 2000)
    private String remark; // 비고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee; // 휴가 신청자

    @OneToOne
    @JoinColumn(name = "approval_id", referencedColumnName = "id", nullable = false)
    private Approval approval; // 결재 정보
}
