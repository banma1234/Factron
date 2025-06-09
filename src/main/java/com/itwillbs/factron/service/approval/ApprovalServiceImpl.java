package com.itwillbs.factron.service.approval;

import com.itwillbs.factron.dto.approval.RequestApprovalDTO;
import com.itwillbs.factron.dto.approval.ResponseSearchApprovalDTO;
import com.itwillbs.factron.dto.approval.RequestSearchApprovalDTO;
import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import com.itwillbs.factron.entity.Transfer;
import com.itwillbs.factron.mapper.approval.ApprovalMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import com.itwillbs.factron.repository.transfer.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRespository;
    private final ApprovalMapper approvalMapper;
    private final EmployeeRepository employeeRepository;
    private final TransferRepository transferRepository;
    private final IntergratAuthRepository intergratAuthRepository;

    @Override
    public List<ResponseSearchApprovalDTO> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO){
        return approvalMapper.getApprovalList(requestSearchApprovalDTO);
    }

//    @Override
//    @Transactional
//    public void updateApproval(RequestApprovalDTO requestApprovalDTO) {
//        Approval approval = approvalRespository.findById(requestApprovalDTO.getApprovalId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 결재 내역을 찾을 수 없습니다."));
//
//        // 👇 진짜 영속 Employee 객체 조회
//        Employee approver = employeeRepository.findById(requestApprovalDTO.getApproverId())
//                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));
//
//        //결재,반려
//        if ("APV002".equals(requestApprovalDTO.getApprovalStatus())) {
//            approval.approve(approver);
//        } else if ("APV003".equals(requestApprovalDTO.getApprovalStatus())) {
//            approval.reject(approver, requestApprovalDTO.getRejectionReason());
//        } else {
//            throw new IllegalArgumentException("유효하지 않은 결재 상태 코드입니다.");
//        }
//
//        // JPA가 변경 감지로 자동 반영하므로 save 생략 가능 (영속 상태일 때)
//    }
    @Override
    @Transactional
    public void updateApproval(RequestApprovalDTO requestApprovalDTO) {
        Approval approval = approvalRespository.findById(requestApprovalDTO.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 내역을 찾을 수 없습니다."));

        Employee approver = employeeRepository.findById(requestApprovalDTO.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));

        if ("APV002".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.approve(approver);

            // 👇 인사 발령 승인 추가 로직
            if ("APR003".equals(requestApprovalDTO.getApprovalType())) {
                Transfer transfer = transferRepository.findByApprovalId(approval.getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 결재에 대한 발령 정보가 없습니다."));

                Employee targetEmp = transfer.getEmployee();

                switch (transfer.getTransferTypeCode()) {
                    case "TRS001": // 승진
                        targetEmp.updatePositionCode(transfer.getPositionCode()); // 👈 메소드 필요
                        break;
                    case "TRS002": // 전보
                        targetEmp.updateDeptCode(transfer.getCurrDeptCode()); // 👈 메소드 필요

                        if ("DEP001".equals(transfer.getCurrDeptCode())) {
                            IntergratAuth auth = intergratAuthRepository.findByEmployee(targetEmp)
                                    .orElseThrow(() -> new IllegalArgumentException("해당 직원의 로그인 정보가 없습니다."));
                            auth.updateAuthCode("ATH002"); // 👈 메소드 필요
                        }
                        break;
                }

                transfer.updateTransferDate(LocalDate.now());
            }

        } else if ("APV003".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.reject(approver, requestApprovalDTO.getRejectionReason());
        } else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태 코드입니다.");
        }
    }

}
