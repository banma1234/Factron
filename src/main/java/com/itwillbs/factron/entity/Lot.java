package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.BaseEntity;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Material;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lot")
public class Lot extends com.itwillbs.factron.entity.BaseEntity {

    @Id
    private String id; // LOT ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // 제품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material; // 자재 ID

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType; // 단계 구분 (예: 입고, 조립, 단조, 감사 등 문자열 입력)

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long subtractAsMuchAsPossible(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");
        }
        Long subtracted = Math.min(this.quantity, amount);
        this.quantity -= subtracted;
        return subtracted; // 실제 차감된 수량 리턴
    }
}
