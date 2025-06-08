package com.itwillbs.factron.dto.commute;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommuteDTO {

    private String startDate; // 조회 시작 날짜 (yyyy-MM-dd)
    private String endDate; // 조회 종료 날짜 (yyyy-MM-dd)
    private String nameOrId; // 사원명 또는 사번
    private String dept; // 부서 코드
}
