package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestProcessLotDTO {

    @NotBlank(message = "LOT id는 필수 입력값입니다.")
    private String id;

    private String item_id;
    private String material_id;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    @Pattern(regexp = "^PCS\\d{3}$", message = "공정 LOT 입력시 올바른 공통코드 형식을 따라야 합니다.")
    private String event_type;

    public RequestProcessLotDTO(String id, String item_id, String material_id, Long quantity, String event_type) {
        this.id = id;
        this.item_id = item_id;
        this.material_id = material_id;
        this.quantity = quantity;
        this.event_type = event_type;
    }

    public Lot toEntity(Item item, Material material) {
        return Lot.builder()
                .id(this.id)
                .item(item)
                .material(material)
                .quantity(this.quantity)
                .eventType(this.event_type)
                .createdBy(20180924L)
                .build();
    }
}
