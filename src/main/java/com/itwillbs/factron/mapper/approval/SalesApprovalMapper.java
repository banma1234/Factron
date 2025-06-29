package com.itwillbs.factron.mapper.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalesApprovalMapper {
    List<ResponseSearchSalesApprovalDTO> getSalesApprovalList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO);
    void updateSalesApproval(RequestApprovalDTO requestApprovalDTO);
}
