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
@Table(name = "bom")
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // BOM ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parant_item_id", referencedColumnName = "id", nullable = false)
    private Item parentItem; // 상위 제품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_item_id", referencedColumnName = "id")
    private Item childItem; // 하위 제품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_material_id", referencedColumnName = "id")
    private Material childMaterial; // 하위 자재

    @Column(name = "consumption", nullable = false)
    private Long consumption; // 소모량
}
