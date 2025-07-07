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
@Table(name = "work_order")
public class WorkOrder {

    @Id
    private String id; // 작업 지시 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", referencedColumnName = "id", nullable = false)
    private ProductionPlanning productionPlanning; // 생산계획 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item; // 제품 ID

    @Column(name = "quantity")
    private Long quantity; // 수량

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 상태 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)
    private Line line; // 작업 라인 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // 작업 지시 등록자 (사원 ID)

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 작업 시작일

    // 작업 상태 변경
    public void updateStatus(String statusCode) {
        this.statusCode = statusCode;
    }
}
