package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SalesApprovalService {
    List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO);

    //입고 결재 상태 업데이트
    void updateSalesApproval(RequestSalesApprovalDTO requestSalesApprovalDTO);
}
