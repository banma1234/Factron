package com.itwillbs.factron.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchPurchaseDTO {
    private Long approvalId;          // 결재번호
    private Long purchaseId;          // 발주 ID
    private Long employeeId;          // 발주자 사번
    private String employeeName;      // 발주자 이름
    private Long clientId;            // 거래처 ID
    private String clientName;        // 거래처명
    private String statusCode;        // 상태 코드
    private String statusName;        // 상태명 (공통코드명)
    private LocalDate createdAt;      // 발주 등록일
    private String itemSummary;       // 자재 요약 (원자재명 + 외 n건)
    private Long totalAmount;
}
