package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;

import java.util.List;

public interface ApprovalService {
    // 인사결재 전체 조회
    List<ResponseSearchApprovalDTO> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO);

    // 인사결재 단일 조회
    ResponseSearchApprovalDTO getApprovalById(Long approvalId);

    //결재(승인, 반려)
    void updateApproval(RequestApprovalDTO requestApprovalDTO);


}
