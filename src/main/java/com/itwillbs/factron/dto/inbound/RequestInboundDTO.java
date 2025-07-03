package com.itwillbs.factron.dto.inbound;

import lombok.Data;

@Data
public class RequestInboundDTO {
    private String materialId;
    private Long quantity;
    private Long storageId;
}
