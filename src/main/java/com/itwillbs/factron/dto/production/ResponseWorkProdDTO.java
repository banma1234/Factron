package com.itwillbs.factron.dto.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWorkProdDTO {
    // 작업 제품 또는 투입 품목
    private String prodId;
    private String prodName;
    private String type;
    private String quantity;
    private Long stockQty;
    private String unit;
}
