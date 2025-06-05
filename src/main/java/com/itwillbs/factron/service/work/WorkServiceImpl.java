package com.itwillbs.factron.service.work;

import com.itwillbs.factron.dto.work.RequestWorkDTO;
import com.itwillbs.factron.dto.work.ResponseWorkDTO;
import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.WorkHistory;
import com.itwillbs.factron.mapper.work.WorkMapper;
import com.itwillbs.factron.repository.approval.ApprovalRespository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.work.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final WorkMapper workMapper;
    private final WorkRepository workRepository;
    private final EmployeeRepository empRepository;
    private final ApprovalRespository appRespository;

    /*
    * 근무 목록 조회
    * */
    @Override
    public List<ResponseWorkDTO> getWorkList(RequestWorkDTO requestWorkDTO) {
        return workMapper.getWorkList(requestWorkDTO);
    }

    /*
     * 근무 등록 (결재 추가)
     * */
    @Transactional
    @Override
    public Void registWork(RequestWorkDTO requestWorkDTO) {
        Employee employee = empRepository.findById(requestWorkDTO.getEmpId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원입니다."));

        // 결재 먼저 등록
        Approval approval = appRespository.save(Approval.builder()
                .requester(employee)
                .requestedAt(LocalDate.now())
                .approvalTypeCode("APR001") // 근무
                .approvalStatusCode("APV001") // 미승인
                .build());

        // 근무 등록
        workRepository.save(WorkHistory.builder()
                .workDate(requestWorkDTO.getWorkDate())
                .workCode(requestWorkDTO.getWorkCode())
                .startTime(requestWorkDTO.getStartTime())
                .endTime(requestWorkDTO.getEndTime())
                .employee(employee)
                .approval(approval)
                .build());
        return null;
    }
}
