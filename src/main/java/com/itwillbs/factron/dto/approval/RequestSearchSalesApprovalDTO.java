package com.itwillbs.factron.dto.approval;

import lombok.Data;

@Data
public class RequestSearchSalesApprovalDTO {
    private String startDate;               // 발행일자-시작
    private String endDate;                 // 발행일자-끝
    private String apprType;                // 결재유형
    private String approvalNameOrEmpId;     // 사원이름 or 사원번호
    private String clientName;              // 거래처명

}
