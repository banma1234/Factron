package com.itwillbs.factron.dto.vacation;

import lombok.Data;
import java.time.LocalDate;

@Data
public class VacationRequestDTO {
    //저장컬럼
    private Long empId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;

    //조회컬럼
    private String srhApprovalId;
    private String srhIdOrName;
    private String deptCode;
    private String vacationStartDate;
    private String vacationEndDate;
}