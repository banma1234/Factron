package com.itwillbs.factron.service.quality;


import com.itwillbs.factron.dto.quality.ResponseInspSrhDTO;
import com.itwillbs.factron.entity.QualityInspection;
import com.itwillbs.factron.entity.QualityInspectionStandard;

import java.util.List;

/**
 * 품질 관리 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface QualityInspectionService {
    List<ResponseInspSrhDTO> getQualityInspections();
    void registQualityInspection(QualityInspection qualityInspection);
    void updateQualityInspection(QualityInspection qualityInspection);

    List<QualityInspectionStandard> getQualityInspectionStandards();
    void registQualityInspectionStandard(QualityInspectionStandard qualityInspectionStandard);
    void updateQualityInspectionStandard(QualityInspectionStandard qualityInspectionStandard);
    void deleteQualityInspectionStandard(Long id);
}


