package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.outbound.RequestOutboundDTO;
import com.itwillbs.factron.dto.inbound.RequestInboundDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.approval.SalesApprovalMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.contract.ContractRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import com.itwillbs.factron.repository.purchase.PurchaseRepository;
import com.itwillbs.factron.repository.storage.InboundRepository;
import com.itwillbs.factron.repository.storage.OutboundRepository;
import com.itwillbs.factron.repository.storage.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 영업 결재 관련 비즈니스 로직을 담당하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor // 생성자 주입
@Transactional(readOnly = true) // 기본은 읽기 전용 트랜잭션
public class SalesApprovalServiceImpl implements SalesApprovalService {

    // 의존성 주입: Mapper, Repository 등
    private final ApprovalRepository approvalRepository;
    private final SalesApprovalMapper salesApprovalMapper;
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ItemRepository itemRepository;
    private final MaterialRepository materialRepository;
    private final PurchaseRepository purchaseRepository;
    private final InboundRepository inboundRepository;
    private final StorageRepository storageRepository;
    private final OutboundRepository outboundRepository;

    /**
     * 영업 결재 목록 조회
     */
    @Override
    public List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO request) {
        return salesApprovalMapper.getSalesApprovalList(request);
    }

    /**
     * 단일 결재 상세 조회
     */
    @Override
    public ResponseSearchSalesApprovalDTO getSalesApprovalById(Long approvalId) {
        return salesApprovalMapper.getSalesApprovalById(approvalId);
    }

    /**
     * 결재 승인/반려 처리
     */
    @Override
    @Transactional // 쓰기 작업이 포함되어 있어 readOnly 해제
    public void updateSalesApproval(RequestSalesApprovalDTO dto) {
        // 1. 결재 엔티티 조회
        Approval approval = approvalRepository.findById(dto.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결재를 찾을 수 없습니다."));

        // 2. 결재자(승인 권자) 엔티티 조회
        Employee approver = employeeRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));

        // 3. 결재 유형에 따라 분기 처리
        String approvalType = dto.getApprovalType();

        if ("SLS001".equals(approvalType)) { // 수주 결재
            handleContractApproval(dto, approval, approver);
        } else if ("SLS002".equals(approvalType)) { // 발주 결재
            handlePurchaseApproval(dto, approval, approver);
        } else {
            throw new IllegalArgumentException("처리할 수 없는 결재 유형입니다: " + approvalType);
        }
    }

    /**
     * SLS001: 수주 결재 승인/반려 처리
     */
    private void handleContractApproval(RequestSalesApprovalDTO dto, Approval approval, Employee approver) {
        // 수주 엔티티 조회
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("해당 수주를 찾을 수 없습니다."));

        if ("APV002".equals(dto.getApprovalStatus())) { // 승인
            approval.approve(approver); // 결재 승인 처리
            contract.updateStatus("STP002"); // 수주 상태: 승인 완료(STP002)

            // 출고 창고(ID=3) 조회
            Storage storage = storageRepository.findById(3L)
                    .orElseThrow(() -> new IllegalArgumentException("출고 창고를 찾을 수 없습니다."));

            // 출고 품목 생성
            for (RequestOutboundDTO itemDTO : dto.getOutboundItems()) {
                Item item = itemRepository.findById(itemDTO.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

                Outbound outbound = Outbound.builder()
                        .item(item)
                        .storage(storage)
                        .quantity((long) itemDTO.getQuantity()) // 출고 수량
                        .outDate(null)                          // 출고일 초기값(null)
                        .categoryCode("ITP003")                  // 출고 유형 코드
                        .statusCode("STS001")                    // 출고 상태: 대기(STS001)
                        .contract(contract)                      // 연관 수주
                        .build();

                outboundRepository.save(outbound); // 저장
            }

        } else if ("APV003".equals(dto.getApprovalStatus())) { // 반려
            approval.reject(approver, dto.getRejectionReason()); // 반려 처리 + 사유 기록
            contract.updateStatus("STP003"); // 수주 상태: 반려(STP003)

        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태입니다.");
        }

        // save() 호출 불필요:
        // JPA의 변경 감지(dirty checking)로 트랜잭션 종료 시 자동 반영
    }

    /**
     * SLS002: 발주 결재 승인/반려 처리
     */
    private void handlePurchaseApproval(RequestSalesApprovalDTO dto, Approval approval, Employee approver) {
        // 발주 엔티티 조회
        Purchase purchase = purchaseRepository.findById(dto.getPurchaseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 발주를 찾을 수 없습니다."));

        if ("APV002".equals(dto.getApprovalStatus())) { // 승인
            approval.approve(approver); // 결재 승인 처리
            purchase.updateStatus("STP002"); // 발주 상태: 승인 완료(STP002)

            // 입고 창고(ID=1) 조회
            Storage storage = storageRepository.findById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("입고 창고를 찾을 수 없습니다."));

            // 입고 품목 생성
            for (RequestInboundDTO itemDTO : dto.getPurchaseItems()) {
                Material material = materialRepository.findById(itemDTO.getMaterialId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 자재를 찾을 수 없습니다."));

                Inbound inbound = Inbound.builder()
                        .material(material)
                        .storage(storage)
                        .quantity(itemDTO.getQuantity()) // 입고 수량
                        .inDate(null)                     // 입고일 초기값(null)
                        .categoryCode("ITP001")           // 입고 유형 코드
                        .statusCode("STS001")             // 입고 상태: 대기(STS001)
                        .purchase(purchase)                // 연관 발주
                        .build();

                inboundRepository.save(inbound); // 저장
            }

        } else if ("APV003".equals(dto.getApprovalStatus())) { // 반려
            approval.reject(approver, dto.getRejectionReason()); // 반려 처리 + 사유 기록
            purchase.updateStatus("STP003"); // 발주 상태: 반려(STP003)

        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태입니다.");
        }

        // save() 호출 불필요:
        // JPA의 변경 감지로 트랜잭션 종료 시 DB 반영
    }
}
