package com.itwillbs.factron.dto.approval;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchSalesApprovalDTO {

    private Long approvalId;            // 결재 문서 번호
    private String apprTypeCode;        // 결재 유형 상세 코드 (SLS001, SLS002)
    private String apprTypeName;        // 결재 유형 이름 ex) 수주, 발주

    private Long requesterId;           // 신청자 사번
    private String requesterName;       // 신청자 이름

    private String itemSummary;         // 품목 요약: 품목명 or 품목 외 N건
    private String clientName;          // 거래처명

    private LocalDate requestedAt;      // 신청일자
    private LocalDate confirmedAt;      // 결재일자

    private Long approverId;            // 결재자 사번
    private String approverName;        // 결재자 이름

    private String approvalStatusCode;  // 결재 상태 코드
    private String approvalStatusName;  // 결재 상태 이름 (대기, 승인, 반려)
}
