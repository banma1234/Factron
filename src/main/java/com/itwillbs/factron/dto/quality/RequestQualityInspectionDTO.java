package com.itwillbs.factron.dto.quality;

import lombok.Data;

@Data
public class RequestQualityInspectionDTO {
    private Long inspectionId;
    private String inspectionName;
    private String inspectionType;
    private String inspectionMethod;
}