package com.itwillbs.factron.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePurchaseItemDTO {
    private String materialId;      // 원자재id
    private String materialName;    // 원자재명
    private Long quantity;          // 수량
    private Long price;             // 가격
    private Long amount;            // 총금액
}
