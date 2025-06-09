package com.itwillbs.factron.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTransferDTO {

    private Long id; // 인사발령 ID
    private LocalDate transferDate; // 인사발령 날짜
    private String trsTypeCode; // 인사발령 유형 코드 (예: 부서 이동, 직급 변경 등)
    private String trsTypeName; // 인사발령 유형 이름
    private String positionCode; // 직급 코드 (승진 시 승진 후 직급 코드, 즉 조회 시점에선 현재 직급 코드)
    private String positionName; // 직급 이름 (승진 시 승진 후 직급 이름, 즉 조회 시점에선 현재 직급 이름)
    private Long empId; // 인사발령 대상 직원 ID
    private String empName; // 인사발령 대상 직원 이름
    private String prevDeptCode; // 이전 부서 코드
    private String prevDeptName; // 이전 부서 이름
    private String currDeptCode; // 현재 부서 코드
    private String currDeptName; // 현재 부서 이름
}
