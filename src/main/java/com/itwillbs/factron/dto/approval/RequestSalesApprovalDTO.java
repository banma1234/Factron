package com.itwillbs.factron.dto.approval;

import com.itwillbs.factron.dto.inbound.RequestInboundDTO;
import com.itwillbs.factron.dto.outbound.RequestOutboundDTO;
import lombok.Data;

import java.util.List;

@Data
public class RequestSalesApprovalDTO {
    private Long approvalId;        //결재 번호
    private String approvalType;    //결재 유형
    private Long approverId;        //결재자
    private String approvalStatus;  //승인 여부
    private String rejectionReason; //반려사유

    private Long contractId;
    private List<RequestOutboundDTO> outboundItems;

    private Long purchaseId;
    private List<RequestInboundDTO> purchaseItems;
}
