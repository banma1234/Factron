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
    public List<VacationResponseDTO> getMyVacations(Long empId, LocalDate startDate, LocalDate endDate) {
        log.info("서비스에서 데이터 확인 시작날짜={}. 끝날짜{}",startDate,endDate);


        List<VacationResponseDTO> dtolist = vacationMapper.findMyVacations(empId, startDate.toString(), endDate.toString());
        log.info("dtolist >>>> {}",dtolist);
        return dtolist;
    }

    @Override
    public Void registVacation(Long empId, VacationRequestDTO dto) {
        Employee employee = employeeRepository.findById(empId).orElseThrow();

        boolean hasNonRejected = vacationMapper.VacationCheck(
                empId,
                dto.getVacationStartDate().toString(),
                dto.getVacationEndDate().toString()
        );

        if (hasNonRejected) {
            throw new IllegalStateException("이미 해당 날짜에 신청한 휴가가 있습니다.");
        }

        Long randomEmpId = vacationMapper.selectRandomHrManagerId(empId);
        Employee approver = employeeRepository.findById(randomEmpId).orElseThrow();

        //수정예정
        Approval approval = Approval.builder()
                .requester(employee)
                .approver(approver)
                .approvalStatusCode("wait")
                .approvalTypeCode("vac")
                .requestedAt(LocalDate.now())
                .build();


        approvalRepository.save(approval);

        VacationHistory vacation = dto.toEntity(employee, approval);
        vacationRepository.save(vacation);

        return null;
    }
}