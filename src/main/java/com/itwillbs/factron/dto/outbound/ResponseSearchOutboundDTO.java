package com.itwillbs.factron.dto.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSearchOutboundDTO {
    private Long outboundId;          // 출고 ID
    private String itemId;            // 제품 ID
    private String itemName;          // 제품 이름
    private String materialId;        // 자재 ID
    private String materialName;      // 자재 이름
    private Long storageId;           // 창고 ID
    private String storageName;       // 창고 이름
    private Long quantity;            // 출고 수량
    private LocalDate outDate;        // 출고 날짜
    private String categoryCode;      // 출고 품목 구분 코드
    private String categoryName;      // 출고 품목 구분 이름 (공통코드 ITP)
    private String statusCode;        // 출고 상태 코드
    private String statusName;        // 출고 상태 이름 (공통코드 STS)
}
