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
@Table(name = "quality_inspection_history")
public class QualityInspectionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 품질 검사 이력 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_inspection_id", referencedColumnName = "id", nullable = false)
    private QualityInspection qualityInspection; // 품질 검사 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id", nullable = false)
    private WorkOrder workOrder; // 작업 지시 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item; // 제품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", referencedColumnName = "id")
    private Lot lot; // LOT ID

    @Column(name = "inspection_date")
    private LocalDate inspectionDate; // 검사 날짜 (YYYY-MM-DD 형식)

    @Column(name = "result_value")
    private Double resultValue; // 검사 결과 값

    @Column(name = "result_code", length = 6)
    private String resultCode; // 검사 결과 코드 (예: 합격, 불합격)

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 상태 코드 (예: 대기, 완료)

    // 품질 검사 이력 업데이트 메소드
    public void updateInspectionHistory(
            Lot lot,
            LocalDate inspectionDate,
            Double resultValue,
            String resultCode,
            String statusCode) {
        this.lot = lot;
        this.inspectionDate = inspectionDate;
        this.resultValue = resultValue;
        this.resultCode = resultCode;
        this.statusCode = statusCode;
    }
}
