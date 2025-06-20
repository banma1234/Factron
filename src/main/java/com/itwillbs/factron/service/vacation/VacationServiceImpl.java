package com.itwillbs.factron.service.vacation;

import com.itwillbs.factron.dto.vacation.VacationRequestDTO;
import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.entity.Approval;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.VacationHistory;
import com.itwillbs.factron.mapper.vacation.VacationMapper;

import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.vacation.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final VacationMapper vacationMapper; 
    private final EmployeeRepository employeeRepository;
    private final ApprovalRepository approvalRepository;


    @Override
    public List<VacationResponseDTO> getMyVacations(VacationRequestDTO dto) {
        return vacationMapper.getVacations(dto);
    }

    @Transactional
    @Override
    public Void registVacation(VacationRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원입니다."));

        //중복 방지
        if (vacationMapper.VacationCheck(dto) > 0) {
            throw new IllegalStateException("이미 해당 날짜에 신청한 휴가가 있습니다.");
        }

        //결재 등록
        Approval approval = approvalRepository.save(Approval.builder()
                .requester(employee)
                .approvalStatusCode("APV001")
                .approvalTypeCode("APR002")
                .requestedAt(LocalDate.now())
                .build());

        //휴가 기록 등록
        vacationRepository.save(VacationHistory.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .remark(dto.getRemark())
                .employee(employee)
                .approval(approval)
                .build());

        return null;
    }
}