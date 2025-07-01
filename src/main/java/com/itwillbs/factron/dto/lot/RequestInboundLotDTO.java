package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Material;
import com.itwillbs.factron.entity.enums.LotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * (입고) LOT생성 요청 DTO
 * */
@Data
public class RequestInboundLotDTO {

    @NotBlank(message = "자재는 필수 입력값입니다.")
    private Material material;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotNull(message = "유형 구분은 필수 입력값입니다.")
    private LotType event_type;

    public RequestInboundLotDTO(Material material, Long quantity, LotType event_type) {
        this.material = material;
        this.quantity = quantity;
        this.event_type = event_type;
    }

    /**
     * DTO => Entity 변환 메서드
     * */
    public Lot toEntity(String LotId) {
        return Lot.builder()
                .id(LotId)
                .item(null)
                .material(this.material)
                .quantity(this.quantity)
                .eventType(this.event_type.getPrefix())
                .createdBy(20180924L)
                .build();
    }
}
