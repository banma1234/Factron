package com.itwillbs.factron.dto.lot;

import lombok.Data;

@Data
public class LotTreeDTO {

    private String id;
    private String itemId;
    private String materialId;
    private Long quantity;
    private String eventType;
    private String parentId;
}
