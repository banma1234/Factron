package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.Material;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * (공정) LOT생성 요청 DTO
 * */
@Data
public class RequestProcessLotDTO {

    private Material material;
    private Item item;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    @Pattern(regexp = "^PTP\\d{3}$", message = "공정 LOT 입력시 올바른 공통코드 형식을 따라야 합니다.")
    private String event_type;

    private String work_order_id;

    public RequestProcessLotDTO(Material material, Item item, String event_type, String work_order_id) {
        this.material = material;
        this.item = item;
        this.event_type = event_type;
        this.work_order_id = work_order_id;
    }

    /**
     * DTO => Entity 변환 메서드
     * */
    public Lot toEntity(String LotId, Long currentUser) {
        return Lot.builder()
                .id(LotId)
                .item(this.item)
                .material(this.material)
                .quantity(0L)
                .eventType(this.event_type)
                .createdBy(currentUser)
                .build();
    }
}
