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
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRespository;            // 결재 엔티티 저장소
    private final ApprovalMapper approvalMapper;                     // 결재 조회용 Mapper (MyBatis 또는 유사 Mapper)
    private final EmployeeRepository employeeRepository;             // 직원 정보 저장소
    private final TransferRepository transferRepository;             // 인사 발령 정보 저장소
    private final IntergratAuthRepository intergratAuthRepository;   // 통합 권한 정보 저장소

    // 결재 목록 조회
    @Override
    public List<ResponseSearchApprovalDTO> getApprovalsList(RequestSearchApprovalDTO requestSearchApprovalDTO){
        return approvalMapper.getApprovalList(requestSearchApprovalDTO);
    }

    // 결재 상태 업데이트 (승인 또는 반려)
    @Override
    @Transactional // 쓰기 작업이므로 트랜잭션 설정
    public void updateApproval(RequestApprovalDTO requestApprovalDTO) {
        // 결재 내역 조회
        Approval approval = approvalRespository.findById(requestApprovalDTO.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결재 내역을 찾을 수 없습니다."));

        // 결재자(승인자) 조회
        Employee approver = employeeRepository.findById(requestApprovalDTO.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("결재자를 찾을 수 없습니다."));

        // 결재 승인 처리
        if ("APV002".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.approve(approver); // 승인 처리 (결재 엔티티 내 메서드 호출)

            // 인사 발령(APR003) 유형일 경우 추가 처리
            if ("APR003".equals(requestApprovalDTO.getApprovalType())) {

                // 해당 결재의 발령 정보 조회
                Transfer transfer = transferRepository.findByApprovalId(approval.getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 결재에 대한 발령 정보가 없습니다."));

                // 발령 대상 직원 조회
                Employee targetEmp = transfer.getEmployee();

                // 발령 유형에 따라 분기 처리
                switch (transfer.getTransferTypeCode()) {
                    case "TRS001": // 승진
                        targetEmp.updatePositionCode(transfer.getPositionCode()); // 직급 업데이트
                        break;
                    case "TRS002": // 전보
                        targetEmp.updateDeptCode(transfer.getCurrDeptCode()); // 부서 업데이트

                        // 전보된 부서가 DEP001인 경우, 권한 변경
                        if ("DEP001".equals(transfer.getCurrDeptCode())) {
                            IntergratAuth auth = intergratAuthRepository.findByEmployee(targetEmp)
                                    .orElseThrow(() -> new IllegalArgumentException("해당 직원의 로그인 정보가 없습니다."));
                            auth.updateAuthCode("ATH002"); // 권한 코드 변경
                        }
                        break;
                }

                // 발령일 업데이트
                transfer.updateTransferDate(LocalDate.now());
            }

        }
        // 결재 반려 처리
        else if ("APV003".equals(requestApprovalDTO.getApprovalStatus())) {
            approval.reject(approver, requestApprovalDTO.getRejectionReason());

        }
        // 유효하지 않은 결재 상태일 경우 예외 처리
        else {
            throw new IllegalArgumentException("유효하지 않은 결재 상태 코드입니다.");
        }

        // JPA의 변경 감지를 통해 DB에 자동 반영되므로 별도 save 호출 불필요
    }

}
