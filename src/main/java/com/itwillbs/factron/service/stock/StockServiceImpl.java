package com.itwillbs.factron.service.stock;

import com.itwillbs.factron.dto.stock.RequestStockSrhDTO;
import com.itwillbs.factron.dto.stock.ResponseStockSrhDTO;
import com.itwillbs.factron.repository.storage.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class StockServiceImpl implements StockService{
    private final StockRepository stockRepository;
    
    @Override
    public List<ResponseStockSrhDTO> searchStocks(RequestStockSrhDTO requestDTO) {
        return stockRepository.findStockWithProductInfo(
            requestDTO.getSrhIdOrName(), 
            requestDTO.getStorageId(), 
            requestDTO.getProductTypeCode()
        );
    }
}