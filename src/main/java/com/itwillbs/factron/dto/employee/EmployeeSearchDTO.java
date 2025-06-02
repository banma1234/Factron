package com.itwillbs.factron.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchDTO {
    private Long id;              // 사번
    private String name;          // 이름
    private String dept_code;      // 부서 코드
    private String position_code;  // 직급 코드
    private String is_active;      // 통합 인증 여부
}
