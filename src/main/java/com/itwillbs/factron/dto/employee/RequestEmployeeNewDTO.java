package com.itwillbs.factron.dto.employee;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEmployeeNewDTO {
    private String empName; // 사원 이름
    private String birth; // 주민번호
    private String rrnBack; // 주민번호
    private String email; // 이메일
    private String gender; // 성별
    private String phone; // 전화번호
    private String address; // 주소
    private String joinedDate; // 입사일
    private String eduLevelCode; // 학력 코드
    private String positionCode; // 직급 코드
    private String deptCode; // 부서 코드
    private String employCode; //입사 유형 코드
    private String isActive; // 재직 상태

    public Employee toEntity(Long newId, Long addBy) {
        return Employee.builder()
                .id(newId)
                .employCode(employCode)
                .deptCode(deptCode)
                .positionCode(positionCode)
                .eduLevelCode(eduLevelCode)
                .name(empName)
                .address(address)
                .email(email)
                .phone(phone)
                .birth(birth)
                .rrnBack(rrnBack)
                .gender(gender)
                .joinedDate(joinedDate != null && !joinedDate.isBlank() ? LocalDate.parse(joinedDate) : null)
                .createdBy(addBy)
                .build();
    }

    public IntergratAuth toIntergratAuth(Employee employee) {
        return IntergratAuth.builder()
                .password(phone)
                .authCode(positionCode)
                .isActive(isActive)
                .employee(employee)
                .build();
    }
}
