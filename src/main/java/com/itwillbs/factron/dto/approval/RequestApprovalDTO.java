package com.itwillbs.factron.dto.approval;

import lombok.Data;

@Data
public class RequestApprovalDTO {
    private Long approvalId;        //결재 번호
    private String approvalType;    //결재 유형
    private Long approverId;        //결재자
    private String approvalStatus;  //승인 여부
    private String rejectionReason; //반려사유
}
