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
@Table(name = "work_history")
public class WorkHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 작업 이력 ID (기본키)

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate; // 작업 날짜

    @Column(name = "start_time", length = 5, nullable = false)
    private String startTime; // 작업 시작 시간

    @Column(name = "end_time", length = 5, nullable = false)
    private String endTime; // 작업 종료 시간

    @Column(name = "work_code", length = 6, nullable = false)
    private String workCode; // 작업 코드 (예: 업무 유형 코드)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "approval_id", referencedColumnName = "id", nullable = false)
    private Approval approval; // 결재 정보
}
