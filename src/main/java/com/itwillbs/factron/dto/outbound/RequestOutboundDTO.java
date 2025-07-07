package com.itwillbs.factron.dto.outbound;

import lombok.Data;

@Data
public class RequestOutboundDTO {
    private String itemId;      // 제품id
    private String itemName;    // 제품명
    private Long quantity;      // 수량
    private Long price;         // 가격
    private Long amount;        // 총금액
}
