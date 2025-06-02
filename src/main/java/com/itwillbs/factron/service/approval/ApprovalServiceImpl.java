package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.ResponseApproval;
import com.itwillbs.factron.dto.approval.RequestApproval;
import com.itwillbs.factron.mapper.approval.ApprovalMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRespository;
    private final ApprovalMapper approvalMapper;
    @Override
    public List<ResponseApproval> getApprovalsList(RequestApproval requestApproval){
        return approvalMapper.getApprovalList(requestApproval);
    }
}
