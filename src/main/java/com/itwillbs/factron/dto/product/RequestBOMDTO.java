package com.itwillbs.factron.dto.product;

import lombok.Data;

@Data
public class RequestBOMDTO {

    // 저장 컬럼
    private Long id;
    private String parentItemId;
    private String childItemId;
    private String childMaterialId;
    private Long consumption;

    // 조회 조건
    private String srhIdOrName;
}
