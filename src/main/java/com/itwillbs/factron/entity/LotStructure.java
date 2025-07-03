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
@Table(name = "lot_structure")
public class LotStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // LOT 관계 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_lot_id", referencedColumnName = "id", nullable = false)
    private Lot parentLot; // 상위 LOT ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_lot_id", referencedColumnName = "id", nullable = false)
    private Lot childLot; // 하위 LOT ID
}
