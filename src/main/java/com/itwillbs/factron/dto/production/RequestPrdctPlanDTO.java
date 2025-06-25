package com.itwillbs.factron.dto.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestPrdctPlanDTO {

    // 저장 컬럼
    private String itemId;
    private Long empId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long quantity;

    // 조회 조건
    private String srhId;
    private String srhItemIdOrName;
    private String srhEmpIdOrName;
}
