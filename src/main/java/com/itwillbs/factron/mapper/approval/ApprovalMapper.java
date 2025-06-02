package com.itwillbs.factron.mapper.approval;

import com.itwillbs.factron.dto.approval.RequestApproval;
import com.itwillbs.factron.dto.approval.ResponseApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper {
    List<ResponseApproval> getApprovalList(RequestApproval requestApproval);
}
