package com.itwillbs.factron.service.qualityhistory;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.lot.RequestQualityLotDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestQualityHistoryInfoDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryDTO;
import com.itwillbs.factron.dto.qualityhistory.RequestUpdateQualityHistoryListDTO;
import com.itwillbs.factron.dto.qualityhistory.ResponseQualityHistoryInfoDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.entity.enums.LotType;
import com.itwillbs.factron.mapper.qualityhistory.QualityInspectionHistoryMapper;
import com.itwillbs.factron.repository.lot.LotHistoryRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionHistoryRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionStandardRepository;
import com.itwillbs.factron.repository.storage.InboundRepository;
import com.itwillbs.factron.repository.storage.StorageRepository;
import com.itwillbs.factron.service.inbound.InboundServiceImpl;
import com.itwillbs.factron.service.lot.LotCreateServiceImpl;
import com.itwillbs.factron.service.lotStructure.LotStructureServiceImpl;
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

    // 품질검사 관련 Repository
    private final QualityInspectionHistoryRepository qualityHistoryRepository;
    private final QualityInspectionHistoryMapper qualityHistoryMapper;
    private final QualityInspectionStandardRepository qualityInspectionStandardRepository;

    // 제품, 창고, 입고, 작업지시, LOT 관련 Repository
    private final ItemRepository itemRepository;
    private final StorageRepository storageRepository;
    private final InboundRepository inboundRepository;
    private final WorkOrderRepository workOrderRepository;
    private final LotHistoryRepository lotHistoryRepository;

    // 비즈니스 로직 처리 Service
    private final InboundServiceImpl inboundService;
    private final LotCreateServiceImpl lotCreateService;
    private final LotStructureServiceImpl lotStructureService;

    // 권한 검증 Component
    private final AuthorizationChecker authorizationChecker;

    /**
     * 품질 검사 이력 목록 조회
     *
     * @param requestDto 요청 DTO
     * @return 품질 이력 목록
     */
    @Override
    public List<ResponseQualityHistoryInfoDTO> getQualityHistoryList(RequestQualityHistoryInfoDTO requestDto) {

        log.info("작업지시 ID에 따른 품질 검사 이력 조회: {}", requestDto.getWorkOrderId());

        // 작업지시 ID로 품질 검사 이력 조회
        List<ResponseQualityHistoryInfoDTO> historyList = qualityHistoryMapper.findQualityHistoryByWorkOrderId(requestDto.getWorkOrderId());

        log.info("품질 검사 이력 조회 결과: {}건", historyList.size());

        return historyList;
    }

    /**
     * 품질 검사 이력 결과 저장 및 후속 처리 (메인 비즈니스 로직)
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void updateQualityHistoryList(RequestUpdateQualityHistoryListDTO requestDto) {
        // 권한 검증
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");

        // 1. 제품 정보 조회
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다: " + requestDto.getItemId()));

        // 2. 품질검사용 LOT 생성
        Lot qualityLot = createQualityLot(requestDto, item);

        // 3. LOT 구조 연결 (부모-자식 관계 설정)
        LotHistory lotHistory = lotHistoryRepository.findTopByWorkOrderIdOrderByCreatedAtDesc(requestDto.getWorkOrderId())
                .orElseThrow(() -> new IllegalArgumentException("마지막 공정 LOT을 찾을 수 없습니다."));

        Lot parentLot = lotHistory.getLot();

        lotStructureService.linkLotStructure(parentLot, List.of(qualityLot));

        // 4. 품질검사 결과 처리 및 합격 여부 확인
        boolean allPassed = processQualityInspectionResults(requestDto, item, qualityLot);

        // 5. 작업지시 상태 업데이트
        WorkOrder workOrder = workOrderRepository.findById(requestDto.getWorkOrderId())
                .orElseThrow(() -> new IllegalArgumentException("작업 지시를 찾을 수 없습니다: " + requestDto.getWorkOrderId()));

        workOrder.updateStatus("WKS004");

        // 6. 모든 검사 합격 시 입고 처리, 불합격 시 경고 로그
        if (allPassed) {
            processInboundAndStock(item, requestDto.getFectiveQuantity(), requestDto.getWorkOrderId());
        } else {
            log.warn("불합격 품질검사 항목이 있어 입고 처리를 진행하지 않습니다. 작업지시: {}", requestDto.getWorkOrderId());
        }
    }

    /**
     * 품질검사용 LOT 생성
     *
     * @param requestDto 요청 DTO
     * @param item 제품 엔티티
     * @return 생성된 품질검사 LOT
     */
    private Lot createQualityLot(RequestUpdateQualityHistoryListDTO requestDto, Item item) {

        RequestQualityLotDTO lotDto = RequestQualityLotDTO.builder()
                .event_type(LotType.QUALITY)
                .quantity(requestDto.getFectiveQuantity())
                .item(item)
                .build();

        Lot qualityLot = lotCreateService.CreateQualityLot(lotDto);
        log.info("품질 검사 LOT 생성 완료 - LOT ID: {}", qualityLot.getId());
        return qualityLot;
    }

    /**
     * 모든 품질검사 결과 처리 및 전체 합격 여부 반환
     *
     * @param requestDto 요청 DTO
     * @param item 제품 엔티티
     * @param qualityLot 품질검사 LOT
     * @return 모든 검사 항목 합격 여부
     */
    private boolean processQualityInspectionResults(RequestUpdateQualityHistoryListDTO requestDto, Item item, Lot qualityLot) {

        return requestDto.getQualityHistoryList().stream()
                .allMatch(historyDto -> {
                    // 품질검사 기준 조회
                    QualityInspectionStandard standard = qualityInspectionStandardRepository
                            .findByQualityInspectionIdAndItemId(historyDto.getQualityInspectionId(), item.getId())
                            .orElseThrow(() -> new IllegalArgumentException("품질검사 기준을 찾을 수 없습니다."));

                    // 검사 결과값 및 합격 여부 판정
                    Double resultValue = historyDto.getResultValue();
                    boolean isPassed = resultValue >= standard.getLowerLimit() && resultValue <= standard.getUpperLimit();
                    String resultCode = isPassed ? "QIR001" : "QIR002";

                    // 검사 결과 로그 기록
                    String status = isPassed ? "합격" : "불합격";
                    log.info("품질검사 {} - 품질검사 이력 ID: {}, 결과값: {}, 기준: {} ~ {}",
                            status, historyDto.getQualityHistoryId(), resultValue,
                            standard.getLowerLimit(), standard.getUpperLimit());

                    // 품질검사 이력 엔티티 업데이트
                    QualityInspectionHistory history = qualityHistoryRepository.findById(historyDto.getQualityHistoryId())
                            .orElseThrow(() -> new IllegalArgumentException("품질검사 이력을 찾을 수 없습니다."));

                    // 검사 결과 상태를 완료 (STS003)로 업데이트
                    history.updateInspectionHistory(qualityLot, LocalDate.now(), resultValue, resultCode, "STS003");

                    return isPassed;
                });
    }

    /**
     * 입고 처리 및 재고 수량 업데이트
     *
     * @param item 제품 엔티티
     * @param quantity 입고 수량
     * @param workOrderId 작업지시 ID
     */
    private void processInboundAndStock(Item item, Long quantity, String workOrderId) {

        // 제품 유형에 맞는 창고 조회
        Storage storage = storageRepository.findByTypeCode(item.getTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("해당 제품 유형에 맞는 창고를 찾을 수 없습니다: " + item.getTypeCode()));

        // 입고 엔티티 생성 및 저장
        Inbound inbound = Inbound.builder()
                .item(item)
                .material(null)
                .storage(storage)
                .quantity(quantity)
                .inDate(LocalDate.now())
                .categoryCode(item.getTypeCode())
                .purchase(null)
                .statusCode("STS003")
                .build();

        inboundRepository.save(inbound);

        log.info("제품 입고 처리 완료 - 작업지시: {}, 제품: {}, 수량: {}, 창고: {}",
                workOrderId, item.getId(), quantity, storage.getId());

        // 재고 수량 추가 또는 신규 생성
        inboundService.addOrCreateStock(inbound);
        log.info("재고 수량 업데이트 완료 - 제품: {}, 수량: {}, 창고: {}",
                item.getId(), quantity, storage.getId());
    }
}