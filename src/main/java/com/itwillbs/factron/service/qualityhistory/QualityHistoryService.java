package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;

import java.util.List;

public interface QualityHistoryService {

    // 품질 이력 목록 조회
    List<ResponseQualityHistoryInfoDTO> getQualityHistoryList(RequestQualityHistoryInfoDTO requestDto);
}
