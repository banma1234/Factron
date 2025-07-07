package com.itwillbs.factron.service.purchase;

import com.itwillbs.factron.dto.purchase.RequestRegisterPurchaseDTO;
import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.purchase.PurchaseMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.client.ClientRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import com.itwillbs.factron.repository.purchase.PurchaseListRepository;
import com.itwillbs.factron.repository.purchase.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseServiceImpl implements PurchaseServcie{
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    // 추가된 의존성 주입
    private final ApprovalRepository approvalRepository;
    private final PurchaseListRepository purchaseListRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final MaterialRepository materialRepository;

    // 발주 전체 조회
    @Override
    public List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO){
        return purchaseMapper.getPurchaseList(requestSearchPurchaseDTO);
    }

    // 발주 품목 조회
    @Override
    public List<ResponsePurchaseItemDTO> getPurchaseItemsByPurchaseId(Long purchaseId) {
        return purchaseMapper.getPurchaseItemsByPurchaseId(purchaseId);
    }

    // 발주 상세 조회
    @Override
    public ResponseSearchPurchaseDTO getPurchaseDetailByPurchaseId(Long purchaseId) {
        return purchaseMapper.getPurchaseDetailByPurchaseId(purchaseId);
    }

    // 발주 등록
    @Transactional
    @Override
    public void registerPurchase(RequestRegisterPurchaseDTO dto) {
        // 1. 결재 데이터 저장
        Approval approval = Approval.builder()
                .requester(employeeRepository.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 ID")))
                .requestedAt(LocalDate.now())
                .approvalTypeCode("SLS002") // 발주 결재 코드 (예시)
                .approvalStatusCode("APV001") // 결재대기
                .build();
        approvalRepository.save(approval);

        // 2. 발주 데이터 저장
        Purchase purchase = Purchase.builder()
                .employee(approval.getRequester())
                .client(clientRepository.findById(dto.getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처 ID")))
                .statusCode("STP001") // 발주대기
                .createdAt(LocalDate.now())
                .approval(approval)
                .build();
        purchaseRepository.save(purchase);

        // 3. 발주 품목 저장
        for (RequestRegisterPurchaseDTO.PurchaseItemDTO itemDto : dto.getItems()) {
            Material material = materialRepository.findById(itemDto.getMaterialId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자재 ID"));
            PurchaseList purchaseItem = PurchaseList.builder()
                    .purchase(purchase)
                    .material(material)
                    .unit(material.getUnit()) // material 엔티티에 unit 필드가 있다고 가정
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .build();
            purchaseListRepository.save(purchaseItem);
        }
    }

    // 발주 취소
    @Transactional
    @Override
    public void cancelPurchase(Long approvalId) {
        // 1. 결재 데이터 조회
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결재 ID"));

        // 2. 연관 발주 데이터 조회
        Purchase purchase = purchaseRepository.findByApproval(approval)
                .orElseThrow(() -> new IllegalArgumentException("결재에 연관된 발주 데이터가 없습니다."));

        // 3. 결재 삭제 (FK 걸려있으므로 purchase.approval를 null로 해제 후 삭제)
        purchase.setApproval(null);
        approvalRepository.delete(approval);

        // 4. 발주 상태를 취소로 변경 (예: STP003)
        purchase.updateStatus("STP005");

        // 5. 변경 사항 저장
        purchaseRepository.save(purchase);
    }

}
