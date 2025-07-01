package com.itwillbs.factron.dto.qualityhistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateQualityHistoryListDTO {

    private String itemId; // 제품 ID

    private String workOrderId; // 작업지시 ID

    private Long fectiveQuantity;

    private List<RequestUpdateQualityHistoryDTO> qualityHistoryList; // 품질검사 이력 리스트
}
