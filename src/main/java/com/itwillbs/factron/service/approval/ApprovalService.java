package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.ResponseApproval;
import com.itwillbs.factron.dto.approval.RequestApproval;

import java.util.List;

public interface ApprovalService {
    //결재 조회
    List<ResponseApproval> getApprovalsList(RequestApproval requestApproval);
}
