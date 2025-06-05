package com.itwillbs.factron.dto.work;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestWorkDTO {

    // 저장 컬럼
    private LocalDate workDate;
    private String workCode;
    private String startTime;
    private String endTime;
    private Long empId;
    private Long appId;

    // 조회 조건
    private String srhIdOrName;
    private String srhStrDate;
    private String srhEndDate;
    private String srhDeptCode;
    private String srhWorkCode;
    private String srhApprovalId;
}
