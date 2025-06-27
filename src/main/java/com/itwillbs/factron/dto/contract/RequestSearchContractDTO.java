package com.itwillbs.factron.dto.contract;

import lombok.Data;

@Data
public class RequestSearchContractDTO {
    private String srhApprovalId;           // 결재번호 (문자열로 처리 가능)
    private String startDate;            // 시작일 (YYYY-MM-DD)
    private String endDate;              // 종료일 (YYYY-MM-DD)
    private String approvalStatusCode;  // 결재 상태 코드 (STP)
    private String approvalNameOrEmpId; // 사번 또는 이름 또는 거래처명 검색어
}
