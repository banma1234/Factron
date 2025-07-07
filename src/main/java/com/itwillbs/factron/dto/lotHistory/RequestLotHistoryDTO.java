package com.itwillbs.factron.dto.lotHistory;

import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.LotHistory;
import com.itwillbs.factron.entity.WorkOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LOT 이력 요청 DTO
 * */
@Data
public class RequestLotHistoryDTO {

    @NotBlank(message = "Lot id는 필수 입력값입니다.")
    private String lot_id;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    private WorkOrder work_order;

    @NotNull(message = "생성일은 필수 입력값입니다.")
    private LocalDateTime created_at;

    @Builder
    public RequestLotHistoryDTO(String lot_id, Long quantity, WorkOrder work_order, LocalDateTime created_at) {
        this.lot_id = lot_id;
        this.quantity = quantity;
        this.work_order = work_order;
        this.created_at = created_at;
    }

    public LotHistory toEntity(Lot lot) {
        return LotHistory.builder()
                .lot(lot)
                .quantity(this.quantity)
                .workOrder(this.work_order)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
