package com.itwillbs.factron.entity;

import com.itwillbs.factron.dto.material.RequestMaterialDTO;
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
@Table(name = "material")
public class Material extends BaseEntity {

    @Id
    private String id; // 자재 ID (예: M0000001)

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 자재 이름

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 코드

    @Column(name = "info", length = 100, nullable = false)
    private String info; // 자재 유형

    @Column(name = "spec", length = 255, nullable = false)
    private String spec; // 자재 사양

    public void updateMaterial(RequestMaterialDTO dto) {
        this.unit = dto.getUnit();
        this.name = dto.getName();
        this.info = dto.getInfo();
        this.spec = dto.getSpec();
    }
}
