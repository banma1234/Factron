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
@Table(name = "lot")
public class Lot extends BaseEntity {

    @Id
    private String id; // LOT ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // 제품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material; // 자재 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", referencedColumnName = "id")
    private WorkOrder workOrder; // 작업 지시 ID

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType; // 단계 구분 (예: 입고, 조립, 단조, 감사 등 문자열 입력)
}
