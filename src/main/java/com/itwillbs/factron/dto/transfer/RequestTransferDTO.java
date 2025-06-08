package com.itwillbs.factron.dto.transfer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestTransferDTO {

    // 저장 컬럼
    private String trsTypeCode; // 인사발령 유형 코드
    private Long empId; // 인사발령 대상 직원 ID
    private Long requesterId; // 인사발령 요청자 ID (결재 발행자 ID)
    private LocalDate transDate; // 인사발령 날짜
    private String currPositionCode; // 발령 후 직급 코드
    private String prevDeptCode; // 발령 전 부서 코드
    private String currDeptCode; // 발령 후 부서 코드

    // 조회 조건
    private String srhIdOrName; // 사원 ID 또는 이름으로 조회
    private String srhStrDate; // 조회할 발령 일자 시작 날짜
    private String srhEndDate; // 조회할 발령 일자 종료 날짜
    private String srhTrsTypeCode; // 인사발령 유형 코드로 조회
    private String srhApprovalId; // 인사발령 결재 발행자 ID로 조회
}
