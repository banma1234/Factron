package com.itwillbs.factron.dto.inbound;

import lombok.Data;

@Data
public class RequestInboundDTO {
    private String materialId;  // 원자재id
    private Long quantity;      // 수량
    private Long storageId;     // 창고id
}
