package com.itwillbs.factron.dto.lot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestLotUpdateDTO {

    @NotBlank(message = "자재 id는 필수 입력값입니다.")
    private String material_id;

    @NotNull(message = "자재 개수는 필수 입력값입니다.")
    private Long quantity;

    public RequestLotUpdateDTO(Long quantity, String material_id) {
        this.quantity = quantity;
        this.material_id = material_id;
    }
}
