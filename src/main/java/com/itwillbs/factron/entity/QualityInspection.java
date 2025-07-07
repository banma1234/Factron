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
@Table(name = "quality_inspection")
public class QualityInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quality_inspection_seq")
    @SequenceGenerator(name = "quality_inspection_seq", sequenceName = "quality_inspection_seq", allocationSize = 1)
    private Long id; // 품질 검사 ID

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 검사명

    @Column(name = "inspection_type", length = 50, nullable = false)
    private String inspectionType; // 검사 타입 (예: 두께, 치수, 외관 등)

    @Column(name = "inspection_method", length = 100)
    private String inspectionMethod; // 검사 방법 (예: 육안, 측정기 등)
}
