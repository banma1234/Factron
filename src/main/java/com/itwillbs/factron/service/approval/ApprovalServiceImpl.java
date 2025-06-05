package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;
import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.mapper.approval.ApprovalMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRespository;
    private final ApprovalMapper approvalMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<ResponseSearchApprovalDTO> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO){
        return approvalMapper.getApprovalList(requestSearchApprovalDTO);
    }

    @Override
    @Transactional
    public void updateApproval(RequestApprovalDTO requestApprovalDTO) {
        Approval approval = approvalRespository.findById(requestApprovalDTO.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 내역을 찾을 수 없습니다."));

        // 👇 진짜 영속 Employee 객체 조회
        Employee approver = employeeRepository.findById(requestApprovalDTO.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));

        //결재,반려
        if ("APV002".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.approve(approver);
        } else if ("APV003".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.reject(approver, requestApprovalDTO.getRejectionReason());
        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태 코드입니다.");
        }

        // JPA가 변경 감지로 자동 반영하므로 save 생략 가능 (영속 상태일 때)
    }

}
