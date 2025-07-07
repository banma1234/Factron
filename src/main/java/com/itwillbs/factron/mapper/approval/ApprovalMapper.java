package com.itwillbs.factron.mapper.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    // 인사결재 전체 조회
    List<ResponseSearchApprovalDTO> getApprovalList(RequestSearchApprovalDTO requestSearchApprovalDTO);

    // 인사결재 단일 조회
    ResponseSearchApprovalDTO selectApprovalById(Long approvalId);

    // 인사결재 (승인, 반려)
    void updateApproval(RequestApprovalDTO requestApprovalDTO);
}
