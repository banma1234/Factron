package com.itwillbs.factron.dto.vacation;

import lombok.Data;

@Data
public class VacationResponseDTO {
    private Long empId;
    private String empName;
    private String positionCode;
    private String positionName;
    private String deptCode;
    private String deptName;
    private String vacationStartDate;
    private String vacationEndDate;
    private String remark;

}