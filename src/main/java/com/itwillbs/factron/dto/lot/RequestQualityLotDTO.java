package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.enums.LotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestQualityLotDTO {

    @NotNull(message = "품목은 필수 입력값입니다.")
    private Item item;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotNull(message = "유형 구분은 필수 입력값입니다.")
    private LotType event_type;

    @Builder
    public RequestQualityLotDTO(Item item, Long quantity, LotType event_type) {
        this.item = item;
        this.quantity = quantity;
        this.event_type = event_type;
    }

    public Lot toEntity(String LotId, Long currentUser) {
        return Lot.builder()
                .id(LotId)
                .item(this.item)
                .material(null)
                .quantity(this.quantity)
                .eventType(this.event_type.getPrefix())
                .createdBy(currentUser)
                .build();
    }
}
