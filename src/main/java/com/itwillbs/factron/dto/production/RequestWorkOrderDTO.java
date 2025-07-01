package com.itwillbs.factron.dto.production;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestWorkOrderDTO {

    // 저장 컬럼
    private String planId;
    private String itemId;
    private Long lineId;
    private Long empId;
    private LocalDate startDate;
    private Long quantity;
    private List<RequestWorkProdDTO> inputProds; // 투입 품목
    private List<Long> workers; // 작업자

    // 조회 조건
    private String prdctPlanId;
    private String workOrderId;
    private String itemIdOrName;
    private List<String> statusCodes;
}
