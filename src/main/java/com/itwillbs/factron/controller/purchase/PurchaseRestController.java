package com.itwillbs.factron.controller.purchase;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import com.itwillbs.factron.service.purchase.PurchaseServcie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseRestController {
    private final PurchaseServcie purchaseServcie;

    @GetMapping("")
    public ResponseDTO<List<ResponseSearchPurchaseDTO>> getPurchasesList(RequestSearchPurchaseDTO requestSearchPurchaseDTO) {
        try{
            return ResponseDTO.success(purchaseServcie.getPurchaseList(requestSearchPurchaseDTO));
        }
        catch (Exception e){
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",purchaseServcie.getPurchaseList(requestSearchPurchaseDTO));
        }
    }
}
