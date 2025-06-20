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
@Table(name = "production_planning")
public class ProductionPlanning {

    @Id
    private String id; // 생산계획 ID, 패턴화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item; // 아이템 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; // 생산 계획 담당 직원 ID

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline; // 생산 기한

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 생산 수량
}
