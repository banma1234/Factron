package com.itwillbs.factron.dto.purchase;

import lombok.Data;
import java.util.List;

@Data
public class RequestRegisterPurchaseDTO {
    private Long clientId;                // 거래처 ID
    private Long employeeId;              // 발주 등록자(사원 ID)

    private List<PurchaseItemDTO> items;  // 발주 품목 리스트

    @Data
    public static class PurchaseItemDTO {
        private String materialId;      // 자재 ID (String으로 사용)
        private Long quantity;          // 수량
        private Long price;             // 단가
    }
}
