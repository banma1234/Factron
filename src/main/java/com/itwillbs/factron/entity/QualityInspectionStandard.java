package com.itwillbs.factron.entity;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.QualityInspection;
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
@Table(name = "quality_inspection_standard")
public class QualityInspectionStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quality_inspection_standard_seq")
    @SequenceGenerator(name = "quality_inspection_standard_seq", sequenceName = "quality_inspection_standard_seq", allocationSize = 1)
    private Long id; // 품질 검사 제품별 기준 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_inspection_id", referencedColumnName = "id", nullable = false)
    private QualityInspection qualityInspection; // 품질 검사 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item; // 제품 ID

    @Column(name = "target_value", nullable = false)
    private Double targetValue; // 목표 값

    @Column(name = "upper_limit", nullable = false)
    private Double upperLimit; // 상한 값

    @Column(name = "lower_limit", nullable = false)
    private Double lowerLimit; // 하한 값

    @Column(name = "unit", length = 50, nullable = false)
    private String unit; // 단위
}
