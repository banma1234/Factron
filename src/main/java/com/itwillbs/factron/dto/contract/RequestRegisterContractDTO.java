package com.itwillbs.factron.dto.contract;

import com.itwillbs.factron.dto.purchase.RequestRegisterPurchaseDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestRegisterContractDTO {
    private Long clientId;                // 거래처 ID
    private Long employeeId;              // 발주 등록자(사원 ID)
    private LocalDate deadline;           //기한

    private List<ContractItemDTO> items;  // 발주 품목 리스트

    @Data
    public static class ContractItemDTO {
        private String ItemId;          // 품목 ID (String으로 사용)
        private Long quantity;          // 수량
        private Long price;             // 단가
    }
}
