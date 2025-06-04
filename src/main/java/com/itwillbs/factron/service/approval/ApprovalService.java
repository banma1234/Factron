package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;

import java.util.List;

public interface ApprovalService {
    //결재 조회
    List<ResponseSearchApprovalDTO> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO);

    void updateApproval(RequestApprovalDTO requestApprovalDTO);
}
