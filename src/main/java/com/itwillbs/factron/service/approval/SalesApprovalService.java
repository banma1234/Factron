package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SalesApprovalService {

    // 영업 전체 조회
    List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO);

    // 영업 단일 조회
    ResponseSearchSalesApprovalDTO getSalesApprovalById(Long approvalId);

    //입고 결재 상태 업데이트
    void updateSalesApproval(RequestSalesApprovalDTO requestSalesApprovalDTO);
}
