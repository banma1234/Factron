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
    private String materialId;
    private String materialName;
    private Long quantity;
    private Long price;
    private Long amount; // 또는 BigDecimal 등
}
