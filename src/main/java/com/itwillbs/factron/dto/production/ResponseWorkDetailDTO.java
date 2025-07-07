package com.itwillbs.factron.dto.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWorkDetailDTO {
    private List<ResponseWorkProdDTO> inputProds; // 투입된 품목
    private List<ResponseWorkerDTO> workers; // 작업자
}
