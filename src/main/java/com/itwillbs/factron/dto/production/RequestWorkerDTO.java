package com.itwillbs.factron.dto.production;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestWorkerDTO {

    // 저장 컬럼
//    private String workOrderId;
//    private Long employeeId;

    // 조회 조건
    private String srhOrderId;
}
