package com.itwillbs.factron.dto.employee;

import com.itwillbs.factron.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class RequestEmployeeNewDTO {
    private String empName; // 사원 이름
    private String residentRegistrationNumber; // 주민번호
    private String email; // 이메일
    private String gender; // 성별
    private String phone; // 전화번호
    private String address; // 주소
    private String joinedDate; // 입사일
    private String eduLevelCode; // 학력 코드
    private String positionCode; // 직급 코드
    private String deptCode; // 부서 코드
    private String employeCode; //입사 유형 코드

    public Employee toEntity() {
        return Employee.builder().

            .build();
    }
}
