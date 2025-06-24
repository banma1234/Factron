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
    private Long materialId;
    private String materialName;
    private Integer quantity;
    private Integer price;
    private Integer amount; // 또는 BigDecimal 등
}
