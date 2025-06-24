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
    private Long itemId;
    private String itemName;
    private int quantity;
    private int price;
    private int amount;
}
