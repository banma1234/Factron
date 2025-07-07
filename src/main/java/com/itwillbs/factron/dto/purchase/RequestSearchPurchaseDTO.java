package com.itwillbs.factron.dto.purchase;

import lombok.Data;

@Data
public class RequestSearchPurchaseDTO {
    private String srhApprovalId;           // 결재번호
    private String startDate;               // 시작일
    private String endDate;                 // 종료일
    private String approvalStatusCode;      // 결재 상태 코드
    private String approvalNameOrEmpId;     // 이름 또는 사번 검색어
    private String clientName;              // 거래처명 검색어
}
