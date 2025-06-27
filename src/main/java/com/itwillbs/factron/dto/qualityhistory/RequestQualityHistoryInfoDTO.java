package com.itwillbs.factron.dto.qualityhistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestQualityHistoryInfoDTO {

    private String workOrderId; // 작업지시 ID
}
