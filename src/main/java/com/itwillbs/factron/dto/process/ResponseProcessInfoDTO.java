package com.itwillbs.factron.dto.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProcessInfoDTO {

    private Long processId; // 공정 ID
    private String processName; // 공정 이름
    private String processTypeCode; // 공정 유형 코드
    private String processTypeName; // 공정 유형 이름
    private String description; // 공정 설명
    private Long standardTime; // 공정 기준 시간 (분 단위)
    private String hasMachine; // 설비 여부 (Y/N)
    private String createdAt; // 생성 일시 (yyyy-MM-dd)
    private Long createdBy; // 생성자 사번
}
