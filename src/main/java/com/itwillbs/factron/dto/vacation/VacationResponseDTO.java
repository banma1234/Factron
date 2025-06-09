package com.itwillbs.factron.dto.vacation;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.VacationHistory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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



//    public static VacationResponseDTO fromEntity(VacationHistory vacation) {
//        Employee e = vacation.getEmployee();
//
//        return VacationResponseDTO.builder()
//                .empId(e.getId())
//                .empName(e.getName())
//                .positionCode(e.getPositionCode())
//                .positionName("직급명")
//                .deptCode(e.getDeptCode())
//                .deptName("부서명")
//                .vacationStartDate(vacation.getStartDate().toString())
//                .vacationEndDate(vacation.getEndDate().toString())
//                .remark(vacation.getRemark())
//                .build();
//    }


}