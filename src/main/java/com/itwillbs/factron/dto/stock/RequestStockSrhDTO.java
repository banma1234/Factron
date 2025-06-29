package com.itwillbs.factron.dto.stock;

import lombok.Data;

@Data
public class RequestStockSrhDTO {
    private String srhIdOrName;
    private Long storageId;
    private String productTypeCode;
}
