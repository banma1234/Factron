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
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 인사발령 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee; // 인사발령 대상 직원

    @Column(name = "transfer_type_code", length = 6, nullable = false)
    private String transferTypeCode; // 인사발령 유형 코드 (예: 부서 이동, 직급 변경 등)

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate; // 인사발령 날짜

    @Column(name = "position_code", length = 6, nullable = false)
    private String positionCode; // 직급 코드 (예: 사원, 대리 등)

    @Column(name = "prev_dept_code", length = 6, nullable = false)
    private String prevDeptCode; // 이전 부서 코드

    @Column(name = "curr_dept_code", length = 6, nullable = false)
    private String currDeptCode; // 현재 부서 코드

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", referencedColumnName = "id", nullable = false)
    private Approval approval; // 결재 정보, 인사발령에 대한 결재 정보
}
