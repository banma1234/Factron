package com.itwillbs.factron.mapper.purchase;

import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    // 발주 전체 조회
    List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO);

    // 발주 품목 조회
    List<ResponsePurchaseItemDTO> getPurchaseItemsByPurchaseId(Long purchaseId);

    // 발주 상세 조회
    ResponseSearchPurchaseDTO getPurchaseDetailByPurchaseId(Long purchaseId);
}
