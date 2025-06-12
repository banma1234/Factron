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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final VacationMapper vacationMapper; 
    private final EmployeeRepository employeeRepository;
    private final ApprovalRepository approvalRepository;


    @Override
    public List<VacationResponseDTO> getMyVacations(VacationRequestDTO dto) {
        log.info("사번또는 이름:{}", dto.getSrhIdOrName());
        return vacationMapper.getVacations(dto);
    }

    @Transactional
    @Override
    public Void registVacation(VacationRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmpId()).orElseThrow();

        //중복 방지
        if (vacationMapper.VacationCheck(dto)) {
            throw new IllegalStateException("이미 해당 날짜에 신청한 휴가가 있습니다.");
        }

        //결제 등록
        Approval approval = approvalRepository.save(Approval.builder()
                .requester(employee)
                .approvalStatusCode("APV001")
                .approvalTypeCode("APR002")
                .requestedAt(LocalDate.now())
                .build());

        //휴가 기록 등록
        vacationRepository.save(VacationHistory.builder()
                .startDate(dto.getStartTime())
                .endDate(dto.getEndTime())
                .remark(dto.getRemark())
                .employee(employee)
                .approval(approval)
                .build());


        return null;
    }
}