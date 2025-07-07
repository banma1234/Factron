package com.itwillbs.factron.service.purchase;

import com.itwillbs.factron.dto.purchase.RequestRegisterPurchaseDTO;
import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;

import java.util.List;

public interface PurchaseServcie {

    // 발주 전체 조회
    List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO);

    // 발주 품목 목록 조회
    List<ResponsePurchaseItemDTO> getPurchaseItemsByPurchaseId(Long purchaseId);

    // 발주 상세 조회
    ResponseSearchPurchaseDTO getPurchaseDetailByPurchaseId(Long purchaseId);

    // 발주 등록
    void registerPurchase(RequestRegisterPurchaseDTO dto);

    // 발주 취소
    void cancelPurchase(Long approvalId);
}
