package com.itwillbs.factron.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseWorkDTO {

    private Long id;
    private LocalDate workDate;
    private String startTime;
    private String endTime;
    private String workCode;
    private String workName;
    private Long empId;
    private String empName;
    private String deptCode;
    private String deptName;
    private String positionCode;
    private String positionName;
    private String approvalId;
}
