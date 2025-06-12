package com.itwillbs.factron.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEmployeeSrhDTO {
    private String nameOrId;          // 이름
    private String dept;      // 부서 코드
    private String position;  // 직급 코드
    private String empIsActive;      // 재직 여부
}
