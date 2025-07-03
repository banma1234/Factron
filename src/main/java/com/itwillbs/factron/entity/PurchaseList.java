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
@Table(name = "purchase_list")
public class PurchaseList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 발주 원자재 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", referencedColumnName = "id", nullable = false)
    private Purchase purchase; // 발주 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id", nullable = false)
    private Material material; // 원자재 ID

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 (예: kg, m, 개 등)

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @Column(name = "price", nullable = false)
    private Long price; // 금액
}
