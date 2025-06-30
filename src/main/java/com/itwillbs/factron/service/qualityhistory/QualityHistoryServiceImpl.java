package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryListDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.qualityhistory.QualityInspectionHistoryMapper;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.repository.production.WorkPerformanceRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionHistoryRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionStandardRepository;
import com.itwillbs.factron.repository.storage.InboundRepository;
import com.itwillbs.factron.repository.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualityHistoryServiceImpl implements QualityHistoryService {

    private final QualityInspectionHistoryRepository qualityHistoryRepository;
    private final QualityInspectionHistoryMapper qualityHistoryMapper;
    private final QualityInspectionStandardRepository qualityInspectionStandardRepository;

    private final ItemRepository itemRepository;
    private final StorageRepository storageRepository;
    private final InboundRepository inboundRepository;
    private final WorkPerformanceRepository workPerformanceRepository;
    private final WorkOrderRepository workOrderRepository;

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
    public void updateQualityHistoryList(RequestUpdateQualityHistoryListDTO requestDto) {
        // 현재 로그인한 사원 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();
        log.info("현재 로그인한 사원 ID: {}", empId);
        checkAdminPermission(empId); // 관리자 권한 체크

        String itemId = requestDto.getItemId();

        // 작업 지시 내 모든 품질 검사 이력에 대해 합격 여부를 확인
        boolean allPassedInspection = true;

        // TODO: 품질 검사 LOT 생성 함수 호출
        // (파라미터 : LotType.QUALITY, 양품 갯수, 제품 ID, 작업 지시 ID)
        // Lot lot = 품질 검사 LOT 생성 함수(LotType.QUALITY, 양품 갯수, 제품 ID, 작업 지시 ID)

        // 품질 검사 이력 목록 순회
        for (RequestUpdateQualityHistoryDTO historyDTO : requestDto.getQualityHistoryList()) {

            // 품질검사 기준 조회
            QualityInspectionStandard standard = qualityInspectionStandardRepository
                    .findByQualityInspectionIdAndItemId(historyDTO.getQualityInspectionId(), itemId)
                    .orElseThrow(() -> new IllegalArgumentException("품질검사 기준을 찾을 수 없습니다."));

            // 품질검사 이력 결과 값
            Double resultValue = historyDTO.getResultValue();

            // 품질검사 이력 결과 코드
            String resultCode;

            // 품질검사 이력 결과 값이 기준 범위 내에 있는지 확인
            if (resultValue >= standard.getLowerLimit() && resultValue <= standard.getUpperLimit()) {

                resultCode = "QIR001"; // 합격
                log.info("품질검사 합격 - 품질검사 이력 ID: {}, 결과값: {}, 기준: {} ~ {}",
                        historyDTO.getQualityHistoryId(), resultValue,
                        standard.getLowerLimit(), standard.getUpperLimit());
            } else {

                resultCode = "QIR002"; // 불합격
                allPassedInspection = false;
                log.info("품질검사 불합격 - 품질검사 이력 ID: {}, 결과값: {}, 기준: {} ~ {}",
                        historyDTO.getQualityHistoryId(), resultValue,
                        standard.getLowerLimit(), standard.getUpperLimit());
            }

            // 품질검사 이력 엔티티 조회
            QualityInspectionHistory history = qualityHistoryRepository.findById(historyDTO.getQualityHistoryId())
                    .orElseThrow(() -> new IllegalArgumentException("품질검사 이력을 찾을 수 없습니다."));

            // 품질검사 이력 업데이트
            history.updateInspectionHistory(
                    null, // lot
                    LocalDate.now(), // 검사 날짜
                    resultValue, // 검사 결과 값
                    resultCode, // 검사 결과 코드
                    "STS003" // 상태 코드
            );
        }

        // 작업 지시 조회
        String workOrderId = requestDto.getWorkOrderId();

        // 작업 지시 작업 상태 업데이트
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("작업 지시를 찾을 수 없습니다: " + workOrderId));

        workOrder.updateStatus("WKS004"); // 상태 코드: 작업 완료

        // 모든 품질검사 항목이 합격일 때만 입고 처리
        if (allPassedInspection) {

            // 양품 수량 계산
            WorkPerformance workPerformance = workPerformanceRepository
                    .findByWorkOrderId(workOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("작업 실적을 찾을 수 없습니다: " + workOrderId));

            Long fectiveQuantity = workPerformance.getFectiveQuantity();

            // 제품 및 창고 정보 조회
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다: " + itemId));

            Storage storage = storageRepository.findByTypeCode(item.getTypeCode())
                    .orElseThrow(() -> new IllegalArgumentException("해당 제품 유형에 맞는 창고를 찾을 수 없습니다: " + item.getTypeCode()));

            // 입고 처리
            Inbound inbound = Inbound.builder()
                    .item(item)
                    .material(null)
                    .storage(storage)
                    .quantity(fectiveQuantity)
                    .inDate(LocalDate.now())
                    .categoryCode(item.getTypeCode())
                    .purchase(null)
                    .statusCode("STS003")
                    .build();

            inboundRepository.save(inbound);

            log.info("제품 입고 처리 완료 - 작업지시: {}, 제품: {}, 수량: {}, 창고: {}",
                    workOrderId, item.getId(), fectiveQuantity, storage.getId());

            // TODO: 재고 수량 업데이트 등 추가 작업

        } else {

            log.warn("불합격 품질검사 항목이 있어 입고 및 재고 수량 업데이트 처리를 진행하지 않습니다. 작업지시: {}", workOrderId);
        }
    }
}
