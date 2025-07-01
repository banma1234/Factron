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

    private Material material;
    private Item item;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    @Pattern(regexp = "^PCS\\d{3}$", message = "공정 LOT 입력시 올바른 공통코드 형식을 따라야 합니다.")
    private String event_type;

    private String work_order_id;

    public RequestProcessLotDTO(Material material, Item item, Long quantity, String event_type, String work_order_id) {
        this.material = material;
        this.item = item;
        this.quantity = quantity;
        this.event_type = event_type;
        this.work_order_id = work_order_id;
    }

    public Lot toEntity(String LotId) {
        return Lot.builder()
                .id(LotId)
                .item(this.item)
                .material(this.material)
                .quantity(this.quantity)
                .eventType(this.event_type)
                .createdBy(20180924L)
                .build();
    }
}
