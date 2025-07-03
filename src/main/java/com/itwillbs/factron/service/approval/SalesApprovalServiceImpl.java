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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesApprovalServiceImpl implements SalesApprovalService {

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

    @Override
    public List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO) {
        return salesApprovalMapper.getSalesApprovalList(requestSearchSalesApprovalDTO);
    }

    @Override
    public ResponseSearchSalesApprovalDTO getSalesApprovalById(Long approvalId) {
        return salesApprovalMapper.getSalesApprovalById(approvalId);
    }


    @Override
    @Transactional
    public void updateSalesApproval(RequestSalesApprovalDTO dto) {
        Approval approval = approvalRepository.findById(dto.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결재를 찾을 수 없습니다."));

        Employee approver = employeeRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));

        String approvalType = dto.getApprovalType();

        if ("SLS001".equals(approvalType)) {
            // 수주 승인/반려 처리
            handleContractApproval(dto, approval, approver);

        } else if ("SLS002".equals(approvalType)) {
            // 발주 승인/반려 처리
            handlePurchaseApproval(dto, approval, approver);

        } else {
            throw new IllegalArgumentException("처리할 수 없는 결재 유형입니다: " + approvalType);
        }
    }

    private void handleContractApproval(RequestSalesApprovalDTO dto, Approval approval, Employee approver) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("해당 수주를 찾을 수 없습니다."));

        if ("APV002".equals(dto.getApprovalStatus())) {
            approval.approve(approver);
            contract.updateStatus("STP002");
            contractRepository.save(contract);

            Storage storage = storageRepository.findById(3L)
                    .orElseThrow(() -> new IllegalArgumentException("출고 창고를 찾을 수 없습니다."));

            for (RequestOutboundDTO itemDTO : dto.getOutboundItems()) {
                Item item = itemRepository.findById(itemDTO.getItemId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

                Outbound outbound = Outbound.builder()
                        .item(item)
                        .storage(storage)
                        .quantity((long) itemDTO.getQuantity())
                        .outDate(null)
                        .categoryCode("ITP003")
                        .statusCode("STS001")
                        .contract(contract)
                        .build();

                outboundRepository.save(outbound);
            }

        } else if ("APV003".equals(dto.getApprovalStatus())) {
            approval.reject(approver, dto.getRejectionReason());
            contract.updateStatus("STP003");
            contractRepository.save(contract);
        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태입니다.");
        }
    }

    private void handlePurchaseApproval(RequestSalesApprovalDTO dto, Approval approval, Employee approver) {
        Purchase purchase = purchaseRepository.findById(dto.getPurchaseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 발주를 찾을 수 없습니다."));

        if ("APV002".equals(dto.getApprovalStatus())) {
            approval.approve(approver);
            purchase.updateStatus("STP002");
            purchaseRepository.save(purchase);

            Storage storage = storageRepository.findById(1L) // 입고 창고 ID로 변경
                    .orElseThrow(() -> new IllegalArgumentException("입고 창고를 찾을 수 없습니다."));

            for (RequestInboundDTO itemDTO : dto.getPurchaseItems()) {
                Material material = materialRepository.findById(itemDTO.getMaterialId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 자재를 찾을 수 없습니다."));

                Inbound inbound = Inbound.builder()
                        .material(material)
                        .storage(storage)
                        .quantity(itemDTO.getQuantity())
                        .inDate(null)
                        .categoryCode("ITP001")
                        .statusCode("STS001") // 입고 대기
                        .purchase(purchase)
                        .build();

                inboundRepository.save(inbound);
            }

        } else if ("APV003".equals(dto.getApprovalStatus())) {
            approval.reject(approver, dto.getRejectionReason());
            purchase.updateStatus("STP003");
            purchaseRepository.save(purchase);
        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태입니다.");
        }
    }
}