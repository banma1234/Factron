package com.itwillbs.factron.dto.machine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMachineInfoDTO {

    private String machineNameOrManufacturer; // 설비명 or 제조사명
}
