package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;

import java.util.List;

public interface SalesApprovalService {
    List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO);
}
