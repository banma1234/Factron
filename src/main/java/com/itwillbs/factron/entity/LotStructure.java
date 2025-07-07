package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Lot;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lot_structure_seq")
    @SequenceGenerator(name = "lot_structure_seq", sequenceName = "lot_structure_seq", allocationSize = 1)
    private Long id; // LOT 관계 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_lot_id", referencedColumnName = "id", nullable = false)
    private Lot parentLot; // 상위 LOT ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_lot_id", referencedColumnName = "id", nullable = false)
    private Lot childLot; // 하위 LOT ID
}
