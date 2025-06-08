package com.itwillbs.factron.service.transfer;

import com.itwillbs.factron.dto.transfer.RequestTransferDTO;
import com.itwillbs.factron.dto.transfer.ResponseTransferDTO;
import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.Transfer;
import com.itwillbs.factron.mapper.transfer.TransferMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.transfer.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferServiceImpl implements TransferService {

    private final TransferMapper transferMapper;
    private final TransferRepository transferRepository;
    private final EmployeeRepository empRepository;
    private final ApprovalRepository appRepository;

    /**
     * 인사발령 목록 조회
     * @param requestTransferDTO 요청 DTO
     * @return 인사발령 목록
     */
    @Override
    public List<ResponseTransferDTO> getTransferList(RequestTransferDTO requestTransferDTO) {

        return transferMapper.getTransferList(requestTransferDTO);
    }

    /**
     * 인사발령 등록 (결재 추가)
     * @param requestTransferDTO 요청 DTO
     * @return Void
     */
    @Transactional
    @Override
    public Void registTransfer(RequestTransferDTO requestTransferDTO) {

        // 인사발령 대상 직원 조회
        Employee employee = empRepository.findById(requestTransferDTO.getEmpId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 인사발령 대상 사원입니다."));


        // 인사발령 요청자 (결재 발행자) 조회
        Employee requester = empRepository.findById(requestTransferDTO.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 인사발령 요청자 사원입니다."));

        // 중복 결재 체크 (미승인 상태)
        Map<String, Object> params = new HashMap<>();

        params.put("employeeId", requestTransferDTO.getEmpId()); // 인사발령 대상 사원 ID
        params.put("transferTypeCode", requestTransferDTO.getTrsTypeCode()); // 인사발령 유형 코드
        params.put("approvalStatusCode", "APV001"); // 미승인 상태 코드

        // 인사발령 중복 조회
        boolean exists = transferMapper.existsPendingTransfer(params);

        // 중복 결재가 존재하는 경우 예외 처리
        if (exists) {
            throw new IllegalStateException("이미 존재하는 결재건 입니다.");
        }

        // 결재 등록 (오늘 날짜로)
        Approval approval = appRepository.save(Approval.builder()
                .requester(requester) // 결재 발행자 (인사발령 요청자)
                .requestedAt(LocalDate.now()) // 결재 요청 날짜 (오늘 날짜)
                .approvalTypeCode("APR003") // 인사발령
                .approvalStatusCode("APV001") // 미승인 상태
                .build());

        // 인사발령 등록
        transferRepository.save(Transfer.builder()
                .employee(employee) // 인사발령 대상 사원
                .transferTypeCode(requestTransferDTO.getTrsTypeCode()) // 인사발령 유형 코드
                .transferDate(requestTransferDTO.getTransDate()) // 인사발령 날짜
                .positionCode(requestTransferDTO.getCurrPositionCode()) // 현재 직급 코드
                .prevDeptCode(requestTransferDTO.getPrevDeptCode()) // 이전 부서 코드
                .currDeptCode(requestTransferDTO.getCurrDeptCode()) // 현재 부서 코드
                .approval(approval) // 결재 정보
                .build());


        return null;
    }
}
