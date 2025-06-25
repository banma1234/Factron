package com.itwillbs.factron.service.quality;

import com.itwillbs.factron.dto.quality.*;
import com.itwillbs.factron.entity.QualityInspection;
import com.itwillbs.factron.entity.QualityInspectionStandard;

import java.util.List;

/**
 * 품질 관리 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface QualityInspectionService {
    // 품질 검사 관리
    List<ResponseInspSrhDTO> getQualityInspections(RequestInspSrhDTO requestDTO);
    void registQualityInspection(List<RequestQualityInspectionDTO> inspections);
    void updateQualityInspection(List<RequestQualityInspectionDTO> inspections);

    // 제품별 품질 검사 기준 관리
    List<ResponseQualityStandardDTO> getQualityInspectionStandards(RequestInspStdSrhDTO requestDTO);
    List<ResponseQualityStandardDTO> getQualityInspectionStandardsByInspectionId(String inspectionIdOrName);
    void registQualityInspectionStandard(List<RequestQualityStandardDTO> standards);
    void updateQualityInspectionStandard(List<RequestQualityStandardDTO> standards);
    void deleteQualityInspectionStandard(List<RequestQualityStandardDeleteDTO.DeleteItem> deleteList);
}


