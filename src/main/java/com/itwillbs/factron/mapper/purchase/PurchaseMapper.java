package com.itwillbs.factron.mapper.purchase;

import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PurchaseMapper {
    List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO);
    List<ResponsePurchaseItemDTO> getPurchaseItemsByPurchaseId(Long purchaseId);

    ResponseSearchPurchaseDTO getPurchaseDetailByPurchaseId(Long purchaseId);
}
