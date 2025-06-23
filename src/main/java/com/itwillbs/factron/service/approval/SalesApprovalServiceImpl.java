package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestSearchSalesApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchSalesApprovalDTO;
import com.itwillbs.factron.repository.approval.SalesApprovalRepository;
import com.itwillbs.factron.mapper.approval.SalesApprovalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesApprovalServiceImpl implements SalesApprovalService {
    private final SalesApprovalRepository salesApprovalRepository;
    private final SalesApprovalMapper salesApprovalMapper;

    @Override
    public List<ResponseSearchSalesApprovalDTO> getSalesApprovalsList(RequestSearchSalesApprovalDTO requestSearchSalesApprovalDTO){
        return salesApprovalMapper.getSalesApprovalList(requestSearchSalesApprovalDTO);
    }
}
