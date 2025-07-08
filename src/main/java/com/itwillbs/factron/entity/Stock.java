package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Material;
import com.itwillbs.factron.entity.Storage;
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
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq")
    @SequenceGenerator(name = "stock_seq", sequenceName = "stock_seq", allocationSize = 1)
    private Long id; // 재고 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // 재고 제품 정보

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material; // 재고 자재 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id", referencedColumnName = "id", nullable = false)
    private Storage storage; // 재고가 위치한 창고 정보

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 재고 수량

    //재고 입고
    public void addQuantity(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("추가할 수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }
    //채고 출고
    public void subtractQuantity(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalStateException("재고 수량이 부족합니다.");
        }
        this.quantity -= amount;
    }

}
