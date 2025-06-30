package com.itwillbs.factron.dto.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchInboundDTO {

    private Long inboundId;           // 입고 ID
    private String itemId;            // 제품 ID
    private String itemName;          // 제품 이름
    private String materialId;        // 자재 ID
    private String materialName;      // 자재 이름
    private Long storageId;           // 창고 ID
    private String storageName;       // 창고 이름
    private Long quantity;            // 입고 수량
    private LocalDate inDate;         // 입고 날짜
    private String categoryCode;      // 입고 품목 구분 코드 (예: 완제품, 반제품, 자재)
    private String categoryName;      // 입고 품목 구분 이름 (공통코드 ITP)
    private String statusCode;        // 입고 상태 코드 (예: 입고대기, 입고완료)
    private String statusName;        // 입고 상태 이름 (공통코드 STP)
}
