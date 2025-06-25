package com.itwillbs.factron.dto.quality;

import com.itwillbs.factron.entity.QualityInspectionStandard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseQualityStandardDTO {
    private Long qualityInspectionStandardId;
    private Long qualityInspectionId;
    private String itemId;
    private String targetValue;
    private Double upperLimit;
    private Double lowerLimit;
    private String unit;

    public ResponseQualityStandardDTO(QualityInspectionStandard standard) {
        this.qualityInspectionStandardId = standard.getId();
        this.qualityInspectionId = standard.getQualityInspection().getId();
        this.itemId = standard.getItem().getId();
        this.targetValue = standard.getTargetValue().toString();
        this.upperLimit = standard.getUpperLimit();
        this.lowerLimit = standard.getLowerLimit();
        this.unit = standard.getUnit();
    }
} 