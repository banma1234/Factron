package com.itwillbs.factron.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEmployeeSrhDTO {
    private String nameOrId;          // 이름
    private String deptCode;      // 부서 코드
    private String positionCode;  // 직급 코드
    private String empIsActive;      // 재직 여부
}
