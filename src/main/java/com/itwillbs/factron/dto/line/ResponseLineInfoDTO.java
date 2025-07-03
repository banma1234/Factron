package com.itwillbs.factron.dto.line;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLineInfoDTO {

    private Long lineId;    // 라인 ID
    private String lineName;      // 라인 이름
    private String lineStatusCode; // 라인 상태 코드
    private String lineStatusName; // 라인 상태 이름
    private String description;   // 라인 설명
    private String createdAt;     // 생성 일시 (yyyy-MM-dd)
    private Long createdBy;    // 생성자 사번
}
