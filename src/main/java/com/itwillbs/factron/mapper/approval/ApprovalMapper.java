package com.itwillbs.factron.mapper.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponseSearchApprovalDTO> getApprovalList(RequestSearchApprovalDTO requestSearchApprovalDTO);
    void updateApproval(RequestApprovalDTO requestApprovalDTO);
    ResponseSearchApprovalDTO selectApprovalById(Long approvalId);
}
