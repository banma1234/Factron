package com.itwillbs.factron.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 수주 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // 수주 등록자 (사원 ID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client; // 거래처 ID

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline; // 납기일

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 상태 코드 (예: 대기, 완료, 반려, 취소)

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt; // 발주 등록일

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", referencedColumnName = "id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Approval approval; // 결재 정보 (수주 결재 정보)

    public void updateStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }
}
