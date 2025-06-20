package com.itwillbs.factron.entity;

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
@Table(name = "lot_history")
public class LotHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // LOT 이력 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", referencedColumnName = "id", nullable = false)
    private Lot lot; // LOT ID

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id", nullable = false)
    private WorkOrder workOrder; // 작업 지시 ID
}
