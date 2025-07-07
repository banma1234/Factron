package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.WorkOrder;
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
@Table(name = "work_performance")
public class WorkPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_performance_seq")
    @SequenceGenerator(name = "work_performance_seq", sequenceName = "work_performance_seq", allocationSize = 1)
    private Long id; // 작업 실적 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id", nullable = false)
    private WorkOrder workOrder; // 작업 지시 ID

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 작업 종료일

    @Column(name = "fective_quantity", nullable = false)
    private Long fectiveQuantity; // 양품 수량

    @Column(name = "defective_quantity", nullable = false)
    private Long defectiveQuantity; // 불량품 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // 작업 실적 등록자 (사원 ID)
}
