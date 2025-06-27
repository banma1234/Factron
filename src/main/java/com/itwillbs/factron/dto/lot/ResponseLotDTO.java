package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class ResponseLotDTO {

    @NotBlank(message = "Lot id는 필수 입력값입니다.")
    private String id;

    private Item item;
    private Material material;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    private String event_type;

    @Builder
    public ResponseLotDTO(String id, Item item, Material material, Long quantity, String event_type) {
        this.id = id;
        this.item = item;
        this.material = material;
        this.quantity = quantity;
        this.event_type = event_type;
    }
}
