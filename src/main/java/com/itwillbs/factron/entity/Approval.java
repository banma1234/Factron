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
@Table(name = "approval")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private Employee requester; // 발행자

    @Column(name = "requested_at", nullable = false)
    private LocalDate requestedAt; // 발행일

    @Column(name = "approval_type_code", length = 6, nullable = false)
    private String approvalTypeCode; // 결재 유형 코드 (예: 휴가, 출장 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private Employee approver; // 결재자

    @Column(name = "confirmed_at")
    private LocalDate confirmedAt; // 결재일

    @Column(name = "approval_status_code", length = 6, nullable = false)
    private String approvalStatusCode; // 결재 상태 코드 (예: 승인, 반려 등)
}
