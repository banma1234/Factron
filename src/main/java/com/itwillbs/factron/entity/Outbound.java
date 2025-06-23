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
@Table(name = "outbound")
public class Outbound {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 출고 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; // 제품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material; // 자재 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id", referencedColumnName = "id", nullable = false)
    private Storage storage; // 창고 ID

    @Column(name = "quantity", nullable = false)
    private Long quantity; // 출고 수량

    @Column(name = "out_date")
    private LocalDate outDate; // 출고 날짜

    @Column(name = "category_code", length = 6, nullable = false)
    private String categoryCode; // 출고 품목 구분 코드 (예: 완제품, 반제품, 자재)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract; // 수주 ID (수주 출고 시)

    @Column(name = "status_code", length = 6, nullable = false)
    private String statusCode; // 상태 코드 (예: 완료, 대기 -> 완제품만 출고 대기 가능)
}
