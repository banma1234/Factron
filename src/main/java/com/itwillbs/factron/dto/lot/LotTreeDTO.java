package com.itwillbs.factron.dto.lot;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LOT 트리 DTO (LOT + LOT_STRUCTURE)
 * */
@Data
public class LotTreeDTO {

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
}
