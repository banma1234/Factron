package com.itwillbs.factron.dto.lot;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LOT 트리 반환 DTO (LOT + LOT_STRUCTURE)
 * */
@Data
public class ResponseLotTreeDTO {

    @NotBlank(message = "Lot id는 필수 입력값입니다.")
    private String id;

    private String itemId;
    private String materialId;

    @NotNull(message = "수량은 필수 입력값입니다.")
    private Long quantity;

    @NotBlank(message = "유형 구분은 필수 입력값입니다.")
    private String eventType;

    @NotBlank(message = "부모 노드는 필수 입력값입니다.")
    private String parentId;

    @NotNull(message = "생성자는 필수 입력값입니다.")
    private Long createdBy;

    @NotNull(message = "생성일은 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @NotNull(message = "자식 노드는 필수 입력값입니다.")
    private List<ResponseLotTreeDTO> children;

    @Builder
    public ResponseLotTreeDTO(String id, String itemId, String materialId, Long quantity, String eventType, String parentId, Long createdBy, LocalDateTime createdAt, List<ResponseLotTreeDTO> children) {
        this.id = id;
        this.itemId = itemId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.eventType = eventType;
        this.parentId = parentId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.children = children;
    }

}
