package com.itwillbs.factron.dto.purchase;

import lombok.Data;

@Data
public class RequestSearchPurchaseDTO {
    private String srhApprovalId;           // 결재번호 (문자열)
    private String startDate;               // 시작일 (YYYY-MM-DD)
    private String endDate;                 // 종료일 (YYYY-MM-DD)
    private String approvalStatusCode;     // 결재 상태 코드 (STP)
    private String approvalNameOrEmpId;    // 이름 또는 사번 또는 거래처명 검색어
}
