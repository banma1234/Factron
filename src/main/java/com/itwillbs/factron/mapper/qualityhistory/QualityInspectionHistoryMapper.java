package com.itwillbs.factron.mapper.qualityhistory;

import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QualityInspectionHistoryMapper {

    // 작업지시 ID에 따른 품질 검사 이력 조회
    List<ResponseQualityHistoryInfoDTO> findQualityHistoryByWorkOrderId(String workOrderId);
}
