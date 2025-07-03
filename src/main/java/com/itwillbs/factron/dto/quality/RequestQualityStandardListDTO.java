package com.itwillbs.factron.dto.quality;

import lombok.Data;
import java.util.List;

@Data
public class RequestQualityStandardListDTO {
    private List<RequestQualityStandardDTO> qualityInspectionStandard;
} 