package com.itwillbs.factron.dto.machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMachineInfoDTO {

    private Long machineId; // 설비 ID
    private String machineName; // 설비명
    private String manufacturer; // 제조사명
    private Long processId; // 공정 ID
    private String processName; // 공정명
    private String buyDate; // 구입일자 (YYYY-MM-DD 형식)
}
