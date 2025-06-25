package com.itwillbs.factron.dto.quality;

import com.itwillbs.factron.entity.QualityInspection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseInspSrhDTO {
    @NotNull()
    private Long inspectionId;

    @NotNull()
    private String inspectionName;

    @NotNull()
    private String inspectionType;

    private String inspectionMethod;

    /**
     * Entity를 DTO로 변경하는 컨스트럭터
     * @param qualityInspection
     */
    public ResponseInspSrhDTO(QualityInspection qualityInspection) {
        this.inspectionId = qualityInspection.getId();
        this.inspectionName = qualityInspection.getName();
        this.inspectionType = qualityInspection.getInspectionType();
        this.inspectionMethod = qualityInspection.getInspectionMethod();
    }
}
