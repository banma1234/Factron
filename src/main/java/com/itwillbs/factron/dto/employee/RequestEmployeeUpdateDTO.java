package com.itwillbs.factron.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEmployeeUpdateDTO {
    private Long empId;
    private String empName;
    private String residentRegistrationNumber;
    private String email;
    private String gender;
    private String phone;
    private String address;
    private String empIsActive;
    private String joinedDate;
    private String eduLevelCode;
    private String positionCode;
    private String deptCode;
    private String employeCode;
    private String quitDate;
}
