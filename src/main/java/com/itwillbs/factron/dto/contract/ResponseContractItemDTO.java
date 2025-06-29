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
    private String itemId;
    private String itemName;
    private Long quantity;
    private Long price;
    private Long amount;
}
