package com.itwillbs.factron.dto.lotHistory;

import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.LotHistory;
import com.itwillbs.factron.entity.WorkOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestLotHistoryDTO {

    private String lot_id;
    private Long quantity;
    private WorkOrder work_order;
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
