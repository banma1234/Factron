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

    private String processName; // 공정명
    private String hasMachine; // 설비 여부 (Y/N)
}
