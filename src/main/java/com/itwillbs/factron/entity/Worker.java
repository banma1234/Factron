package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.WorkOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "worker")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worker_seq")
    @SequenceGenerator(name = "worker_seq", sequenceName = "worker_seq", allocationSize = 1)
    private Long id; // 작업 담당자 테이블 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id", nullable = false)
    private WorkOrder workOrder; // 작업 지시 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // 작업 담당자 (사원 ID)
}
