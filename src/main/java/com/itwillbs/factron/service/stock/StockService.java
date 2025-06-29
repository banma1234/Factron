package com.itwillbs.factron.service.stock;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.stock.RequestStockSrhDTO;
import com.itwillbs.factron.dto.stock.ResponseStockSrhDTO;

import java.util.List;

public interface StockService {
    // 제품들의 창고별 재고를 조회합니다
    List<ResponseStockSrhDTO> searchStocks(RequestStockSrhDTO requestDTO);
    // 한 제품의 총 재고를 조회합니다.
    List<ResponseStockSrhDTO> getSingleStock(String productId);
}