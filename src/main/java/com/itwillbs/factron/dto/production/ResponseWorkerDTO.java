package com.itwillbs.factron.dto.production;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWorkerDTO {

    private String workOrderId;
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String deptName;
    private String posName;
}
