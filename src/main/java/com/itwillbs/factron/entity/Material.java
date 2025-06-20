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
@Table(name = "material")
public class Material extends BaseEntity {

    @Id
    private String id; // 자재 ID (예: M0000001)

    @Column(name = "name", length = 255, nullable = false)
    private String name; // 자재 이름

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위 (예: EA, kg, L 등)

    @Column(name = "info", length = 100, nullable = false)
    private String info; // 자재 유형

    @Column(name = "spec", length = 255, nullable = false)
    private String spec; // 자재 사양

}
