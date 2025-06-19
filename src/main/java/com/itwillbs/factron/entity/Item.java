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
@Table(name = "item")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // 제품 ID

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 제품 이름

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 (예: EA, kg, L 등)

    @Column(name = "price", nullable = false)
    private Long price; // 가격

    @Column(name = "type_code", length = 6, nullable = false)
    private String typeCode; // 제품 유형 코드 (예: 전자제품, 가전제품 등)
}
