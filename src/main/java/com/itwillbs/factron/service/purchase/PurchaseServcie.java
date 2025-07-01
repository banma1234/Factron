package com.itwillbs.factron.service.purchase;

import com.itwillbs.factron.dto.purchase.RequestRegisterPurchaseDTO;
import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;

import java.util.List;

public interface PurchaseServcie {
    List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO);

    List<ResponsePurchaseItemDTO> getPurchaseItemsByPurchaseId(Long purchaseId);
    ResponseSearchPurchaseDTO getPurchaseDetailByPurchaseId(Long purchaseId);
    void registerPurchase(RequestRegisterPurchaseDTO dto);

    void cancelPurchase(Long approvalId);
}
