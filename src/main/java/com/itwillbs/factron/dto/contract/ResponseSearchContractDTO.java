package com.itwillbs.factron.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchContractDTO {
    private Long approvalId;     // 결재번호
    private Long contractId;     // 수주ID
    private Long employeeId;     // 수주등록자 사번
    private String employeeName; // 수주등록자 이름
    private Long clientId;       // 거래처ID
    private String clientName;   // 거래처명
    private LocalDate deadline;  // 기한 (납기일)
    private String statusCode;   // 상태코드(STP)
    private String statusName;   // 상태코드명
    private LocalDate createdAt; // 등록일

    private String itemSummary;  // 수주 품목명 외 몇건 형식
    private Long totalAmount;    // 수주품목 총금액
}
