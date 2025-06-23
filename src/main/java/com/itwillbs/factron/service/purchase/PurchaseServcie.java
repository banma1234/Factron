package com.itwillbs.factron.service.purchase;

import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;

import java.util.List;

public interface PurchaseServcie {
    List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO);
}
