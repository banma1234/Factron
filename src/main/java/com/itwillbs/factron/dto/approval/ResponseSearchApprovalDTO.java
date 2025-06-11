package com.itwillbs.factron.dto.approval;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchApprovalDTO {
    private Long approvalId;//결재 문서 번호
    private String apprTypeCode;//결재 유형 상세 코드
    private String apprTypeName;//결재 유형 이름 ex)휴가,근무
    private String requesterName;//발행자 이름
    private Long requesterId;//발행자 사번
    private String requestedAt;//발행날짜
    private String positionCode;//직급 상세 코드
    private String positionName;//직급 이름 ex)부장
    private String deptCode;//부서 상세 코드
    private String deptName;//부서 이름 ex)인사
    private String confirmedDate;//결재 날짜
    private String approvalStatusCode;//결재 상태 코드
    private String approvalStatusName;//결재 상태이름 ex)대기,반려
    private String approverName;//결재자 이름
    private Long approverId;//결재자 사번
    private String rejectionReason;//반려사유

    private Long transferEmpId;     // 발령자 사번 (TRANSFER.EMPLOYEE_ID)
    private String transferEmpName; // 발령자 이름 (EMPLOYEE.NAME)

}
