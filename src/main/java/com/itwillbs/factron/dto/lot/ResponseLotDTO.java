package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Lot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class ResponseLotDTO {

    @NotBlank(message = "Lot id는 필수 입력값입니다.")
    private String id;

    private String item_id;
    private String material_id;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    private String event_type;

    @Builder
    public ResponseLotDTO(String id, String item_id, String material_id, Long quantity, String event_type) {
        this.id = id;
        this.item_id = item_id;
        this.material_id = material_id;
        this.quantity = quantity;
        this.event_type = event_type;
    }

    public static ResponseLotDTO fromEntity(Lot entity) {
        return ResponseLotDTO.builder()
                .id(entity.getId())
                .item_id(entity.getItem() != null ? entity.getItem().getId() : null)
                .material_id(entity.getMaterial() != null ? entity.getMaterial().getId() : null)
                .quantity(entity.getQuantity())
                .event_type(entity.getEventType())
                .build();
    }
}
