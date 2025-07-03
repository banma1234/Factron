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
@Table(name = "contract_list")
public class ContractList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 수주 품목 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", referencedColumnName = "id", nullable = false)
    private Contract contract; // 수주 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item; // 품목 ID

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 (예: 개, 박스 등)

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 수량

    @Column(name = "price", nullable = false)
    private Long price; // 금액
}
