package com.itwillbs.factron.dto.quality;

import lombok.Data;

@Data
public class RequestQualityStandardDTO {
    private Long qualityInspectionStandardId;
    private Long qualityInspectionId;
    private String itemId;
    private String targetValue;
    private Double upperLimit;
    private Double lowerLimit;
    private String unit;
} 