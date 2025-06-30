package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.WorkOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestLotUpdateDTO {

    private String material_id ;
    private String item_id;

    @NotNull(message = "자재 개수는 필수 입력값입니다.")
    private Long quantity;

    private WorkOrder work_order_id;

    @Builder
    public RequestLotUpdateDTO(String material_id, String item_id, Long quantity, WorkOrder work_order_id) {
        this.material_id = material_id;
        this.item_id = item_id;
        this.quantity = quantity;
        this.work_order_id = work_order_id;
    }
}
