package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Process;
import com.itwillbs.factron.entity.WorkOrder;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_history_seq")
    @SequenceGenerator(name = "process_history_seq", sequenceName = "process_history_seq", allocationSize = 1)
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

    public void updateStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public void updateInputQuantity(Long inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public void updateOutputQuantity(Long outputQuantity) {
        this.outputQuantity = outputQuantity;
    }

    public void updateCoastTime(Long coastTime) {
        this.coastTime = coastTime;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateLot(Lot lot) {
        this.lot = lot;
    }

}
