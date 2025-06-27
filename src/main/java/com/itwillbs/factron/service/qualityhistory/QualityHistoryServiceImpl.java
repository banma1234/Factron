package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryListDTO;
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

    private final AuthorizationChecker authorizationChecker;

    /**
     * 관리자 권한 체크
     *
     * @param empId 사원 ID
     */
    private void checkAdminPermission(Long empId) {

        boolean hasPermission = true; // TODO: 실제 권한 체크 로직으로 대체

        // 관리자 권한이 없는 경우 예외 처리
        if (!hasPermission) {
            throw new SecurityException("관리자 권한이 없습니다.");
        }
    }

    /**
     * 품질 검사 이력 목록 조회
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

    /**
     * 품질 검사 이력 결과 저장
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void updateQualityHistoryList(List<RequestUpdateQualityHistoryListDTO> requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        checkAdminPermission(empId); // 관리자 권한 체크

        // 품질 검사 LOT 생성 함수 호출
        // (파라미터 : LotType.QUALITY, 양품 갯수, 제품 ID, 작업 지시 ID)
        // Lot lot = 품질 검사 LOT 생성 함수(LotType.QUALITY, 양품 갯수, 제품 ID, 작업 지시 ID)

        // 품질 검사 결과 정의

        // 품질 검사 이력 저장 (수정)

        // 제품 입고 추가 (상태 : 완료)

        // 재고 업데이트 (기존 제품 재고 존재 시 개수 수정, 없을 시 재고 추가)
    }
}
