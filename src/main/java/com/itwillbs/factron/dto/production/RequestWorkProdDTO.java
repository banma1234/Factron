package com.itwillbs.factron.dto.production;

import lombok.Data;

@Data
public class RequestWorkProdDTO {

    // 저장 컬럼
    private String prodId;
    private Long quantity;

    // 조회 조건
    private String parentItemId;
    private String planId;
    private String planQty;
}
