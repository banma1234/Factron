package com.itwillbs.factron.controller.purchase;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.purchase.RequestRegisterPurchaseDTO;
import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponsePurchaseItemDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import com.itwillbs.factron.service.purchase.PurchaseServcie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{purchaseId}/items")
    public ResponseDTO<List<ResponsePurchaseItemDTO>> getPurchaseItems(@PathVariable Long purchaseId) {
        try {
            return ResponseDTO.success(purchaseServcie.getPurchaseItemsByPurchaseId(purchaseId));
        }catch (Exception e){
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",purchaseServcie.getPurchaseItemsByPurchaseId(purchaseId));
        }
    }

    // 단건 상세조회 (approvalId 없는 건도 포함)
    @GetMapping("/{purchaseId}")
    public ResponseDTO<ResponseSearchPurchaseDTO> getPurchaseDetail(@PathVariable Long purchaseId) {
        try {
            ResponseSearchPurchaseDTO dto = purchaseServcie.getPurchaseDetailByPurchaseId(purchaseId);
            return ResponseDTO.success(dto);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "상세 조회에 실패했습니다.", null);
        }
    }


    // 발주 등록 (저장)
    @PostMapping("")
    public ResponseDTO<Void> registerPurchase(@RequestBody RequestRegisterPurchaseDTO requestDto) {
        try {
            purchaseServcie.registerPurchase(requestDto);
            return ResponseDTO.success("등록이 완료되었습니다.",null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "발주 등록에 실패했습니다.", null);
        }
    }

    // 발주 취소
    @PutMapping("/cancel")
    public ResponseDTO<Void> cancelPurchase(@RequestParam Long approvalId) {
        try {
            purchaseServcie.cancelPurchase(approvalId);
            return ResponseDTO.success("발주가 취소되었습니다.", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "발주 취소에 실패했습니다.", null);
        }
    }
}
