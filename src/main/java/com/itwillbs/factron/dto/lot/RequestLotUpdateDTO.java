package com.itwillbs.factron.dto.lot;

import com.itwillbs.factron.entity.WorkOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestLotUpdateDTO {

    @NotBlank(message = "자재 id는 필수 입력값입니다.")
    private String material_id ;

    @NotNull(message = "자재 개수는 필수 입력값입니다.")
    private Long quantity;

    private WorkOrder work_order_id;

    @Builder

    public RequestLotUpdateDTO(String material_id, Long quantity, WorkOrder work_order_id) {
        this.material_id = material_id;
        this.quantity = quantity;
        this.work_order_id = work_order_id;
    }
}
