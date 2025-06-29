package com.itwillbs.factron.dto.item;

import lombok.Data;

@Data
public class ItemRequestDTO {

    // 추가 및 수정용
    private String itemId;
    private String name;
    private String unit;
    private Long price;
    private String typeCode;
    private Long createdBy;

    // 조회용
    private String itemName;
    private String itemStartDate;
    private String itemEndDate;
}
