package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryListDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;

import java.util.List;

public interface QualityHistoryService {

    // 작업 지시 별 품질검사 이력 목록 조회
    List<ResponseQualityHistoryInfoDTO> getQualityHistoryList(RequestQualityHistoryInfoDTO requestDto);

    // 작업 지시 별 품질검사 이력 결과 저장
    void updateQualityHistoryList(List<RequestUpdateQualityHistoryListDTO> requestDto);
}
