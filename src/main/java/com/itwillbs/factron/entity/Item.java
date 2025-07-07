package com.itwillbs.factron.entity;

import com.itwillbs.factron.dto.item.RequestitemDTO;
import com.itwillbs.factron.entity.BaseEntity;
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
@Table(name = "item")
public class Item extends com.itwillbs.factron.entity.BaseEntity {

    @Id
    private String id; // 제품 ID (예: P0000001)

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 제품 이름

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 코드

    @Column(name = "price", nullable = false)
    private Long price; // 가격

    @Column(name = "type_code", length = 6, nullable = false)
    private String typeCode; // 제품 유형 코드

    public void updateItem(RequestitemDTO dto) {
        this.name = dto.getName();
        this.unit = dto.getUnit();
        this.price = dto.getPrice();
        this.typeCode = dto.getTypeCode();
    }
}
