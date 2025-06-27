package com.itwillbs.factron.dto.outbound;

import lombok.Data;

@Data
public class RequestOutboundDTO {
    private String itemId;
    private String itemName;
    private Long quantity;
    private Long price;
    private Long amount;
}
