package com.itwillbs.factron.dto.lot;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LotTreeDTO {

    private String id;
    private String itemId;
    private String materialId;
    private Long quantity;
    private String eventType;
    private String parentId;
    private Long createdBy;
    private LocalDateTime createdAt;
}
