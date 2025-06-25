package com.itwillbs.factron.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseBOMDTO {

    private Long id;
    private String parentItemId;
    private String parentItemName;
    private String childProdId;
    private String childProdName;
    private String prodType;
    private Long consumption;
    private String unitName;
}
