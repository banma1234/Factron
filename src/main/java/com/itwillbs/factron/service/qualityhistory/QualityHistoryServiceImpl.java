package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import com.itwillbs.factron.mapper.qualityhistory.QualityInspectionHistoryMapper;
import com.itwillbs.factron.repository.quality.QualityInspectionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualityHistoryServiceImpl implements QualityHistoryService {

    private final QualityInspectionHistoryRepository qualityHistoryRepository;
    private final QualityInspectionHistoryMapper qualityHistoryMapper;

    /**
     * 품질 이력 목록 조회
     *
     * @param requestDto 요청 DTO
     * @return 품질 이력 목록
     */
    @Override
    public List<ResponseQualityHistoryInfoDTO> getQualityHistoryList(RequestQualityHistoryInfoDTO requestDto) {
        log.info("작업지시 ID에 따른 품질 검사 이력 조회: {}", requestDto.getWorkOrderId());

        List<ResponseQualityHistoryInfoDTO> historyList = qualityHistoryMapper.findQualityHistoryByWorkOrderId(requestDto.getWorkOrderId());

        log.info("품질 검사 이력 조회 결과: {}건", historyList.size());

        return historyList;
    }
}
