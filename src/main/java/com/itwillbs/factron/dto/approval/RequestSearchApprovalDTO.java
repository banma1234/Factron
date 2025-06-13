package com.itwillbs.factron.dto.approval;

import lombok.Data;

@Data
public class RequestSearchApprovalDTO {
    private String startDate;//발행일자-시작
    private String endDate;//발행일자-끝
    private String apprType;//결재유형
    private String dept;//부서
    private String position;//직급
    private String approvalNameOrEmpId;//사원이름or사원번호
}
