package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.enums.LotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestQualityLotDTO {

    @NotBlank(message = "LOT id는 필수 입력값입니다.")
    private String id;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotNull(message = "유형 구분은 필수 입력값입니다.")
    private LotType event_type;

    public RequestQualityLotDTO(String id, Long quantity, LotType event_type) {
        this.id = id;
        this.quantity = quantity;
        this.event_type = event_type;
    }

    public Lot toEntity() {
        return Lot.builder()
                .id(this.id)
                .item(null)
                .material(null)
                .quantity(this.quantity)
                .eventType(this.event_type.getPrefix())
                .createdBy(20180924L)
                .build();
    }
}
