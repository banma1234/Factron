package com.itwillbs.factron.controller.stock;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.stock.RequestStockSrhDTO;
import com.itwillbs.factron.dto.stock.ResponseStockSrhDTO;
import com.itwillbs.factron.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Log4j2
public class StockRestContorller {
   private final StockService stockService;

    @GetMapping("")
    public ResponseDTO<List<ResponseStockSrhDTO>> getStocks(@ModelAttribute RequestStockSrhDTO requestDTO) {
        try {
            List<ResponseStockSrhDTO> stocks = stockService.searchStocks(requestDTO);
            return ResponseDTO.success(stocks);
        } catch (Exception e) {
            log.error("Error searching stocks: ", e);
            return ResponseDTO.fail(500, e.getMessage(), null);
        }
    }
}
