package com.itwillbs.factron.dto.commute;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommuteDTO {

    private Long empId;             // 사번
    private String empName;         // 사원명

    private String positionCode;    // 직급 코드
    private String positionName;    // 직급 명

    private String deptCode;        // 부서 코드
    private String deptName;        // 부서 명

    private String commuteDate;     // 출근 날짜 (yyyy-MM-dd)
    private String commuteIn;       // 출근 시간 (HH:mm:ss)
    private String commuteOut;      // 퇴근 시간 (HH:mm:ss, null 가능)
}
