package com.itwillbs.factron.dto.lot;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseLotTreeDTO {

    private String id;
    private String itemId;
    private String materialId;
    private Long quantity;
    private String eventType;
    private String parentId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<ResponseLotTreeDTO> children;

    @Builder
    public ResponseLotTreeDTO(String id, String itemId, String materialId, Long quantity, String eventType, String parentId, Long createdBy, LocalDateTime createdAt, List<ResponseLotTreeDTO> children) {
        this.id = id;
        this.itemId = itemId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.eventType = eventType;
        this.parentId = parentId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.children = children;
    }

    public static ResponseLotTreeDTO convertLotToResponse(LotTreeDTO dto) {
        return ResponseLotTreeDTO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .materialId(dto.getMaterialId())
                .quantity(dto.getQuantity())
                .eventType(dto.getEventType())
                .parentId(dto.getParentId())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .children(new ArrayList<>())
                .build();
    }
}
