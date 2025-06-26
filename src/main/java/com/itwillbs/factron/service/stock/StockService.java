package com.itwillbs.factron.service.stock;

import com.itwillbs.factron.dto.stock.RequestStockSrhDTO;
import com.itwillbs.factron.dto.stock.ResponseStockSrhDTO;

import java.util.List;

public interface StockService {
    List<ResponseStockSrhDTO> searchStocks(RequestStockSrhDTO requestDTO);
}