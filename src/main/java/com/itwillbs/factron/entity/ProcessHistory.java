package com.itwillbs.factron.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "process_history")
public class ProcessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 공정 이력 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", referencedColumnName = "id", nullable = false)
    private Process process; // 공정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id", nullable = false)
    private WorkOrder workOrder; // 작업 지시 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", referencedColumnName = "id")
    private Lot lot; // LOT ID

    @Column(name = "start_date")
    private LocalDate startDate; // 공정 시작 날짜

    @Column(name = "start_time")
    private LocalDateTime startTime; // 공정 시작 시간

    @Column(name = "coast_time")
    private Long coastTime; // 공정 소요 시간 (분 단위)

    @Column(name = "input_quantity")
    private Long inputQuantity; // 투입 수량

    @Column(name = "output_quantity")
    private Long outputQuantity; // 산출 수량

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 상태 코드 (예: 대기, 시작, 완료)
}
