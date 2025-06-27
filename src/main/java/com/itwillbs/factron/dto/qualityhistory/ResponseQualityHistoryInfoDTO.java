package com.itwillbs.factron.dto.qualityhistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseQualityHistoryInfoDTO {

    private Long qualityHistoryId; // 품질검사 이력 ID
    private Long qualityInspectionId; // 품질검사 ID
    private String qualityInspectionName; // 품질검사 이름
    private String workOrderId; // 작업지시 ID
    private String itemId; // 제품 ID
    private String itemName; // 제품 이름
    private String lotId; // 로트 ID (로트 번호)
    private String inspectionDate; // 검사 일시 (yyyy-MM-dd)
    private String resultValue; // 검사 결과 값
    private String resultCode; // 검사 결과 코드 (예: 합격, 불합격)
    private String resultCodeName; // 검사 결과 코드 이름 (예: 합격, 불합격)
    private String statusCode; // 상태 코드 (예: 대기, 완료)
    private String statusCodeName; // 상태 코드 이름 (예: 대기, 완료)
}
