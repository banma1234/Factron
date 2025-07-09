package com.itwillbs.factron.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseContractItemDTO {
    private String itemId;      // 제품id
    private String itemName;    // 제품명
    private Long quantity;      // 수량
    private Long price;         // 가격
    private Long amount;        // 총금액
    private String unitCode;  // 단위 코드
    private String unitName;  // 단위 이름
}
