package com.itwillbs.factron.dto.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestProcessInfoDTO {

    private String processIdOrName; // 공정 ID 또는 공정명
    private String processTypeCode; // 공정 유형 코드 추가
    private String hasMachine; // 설비 여부 (Y/N)
    private Boolean lineConnected; // 라인에 연결되지 않은 공정만 조회 (true/false)
    private Long lineId; // 라인 ID (라인에 연결된 공정 조회 시 사용)
}
