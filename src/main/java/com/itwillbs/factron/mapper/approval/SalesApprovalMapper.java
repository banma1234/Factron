package com.itwillbs.factron.mapper.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalesApprovalMapper {

    // 영업결재 전체 조회
    List<ResponseSearchSalesApprovalDTO> getSalesApprovalList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO);

    // 영업결재 단일 조회
    ResponseSearchSalesApprovalDTO getSalesApprovalById(Long approvalId);

    // 영업결재 (승인, 반려)
    void updateSalesApproval(RequestApprovalDTO requestApprovalDTO);
}
