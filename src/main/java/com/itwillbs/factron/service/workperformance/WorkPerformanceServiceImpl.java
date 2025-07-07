package com.itwillbs.factron.service.workperformance;

import com.itwillbs.factron.dto.workperformance.RequestWorkPerformanceDTO;
import com.itwillbs.factron.dto.workperformance.ResponseWorkPerformanceDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.WorkOrder;
import com.itwillbs.factron.entity.WorkPerformance;
import com.itwillbs.factron.mapper.workperformance.WorkPerformanceMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.repository.production.WorkPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class WorkPerformanceServiceImpl implements WorkPerformanceService{

    private final WorkPerformanceRepository workPerformanceRepository;
    private final WorkOrderRepository workOrderRepository;
    private final WorkPerformanceMapper workPerformanceMapper;
    private final EmployeeRepository employeeRepository;


    /*
     * 작업 조회
     * */
    @Override
    public List<ResponseWorkPerformanceDTO> getWorkPerformanceList(RequestWorkPerformanceDTO dto) {
        return workPerformanceMapper.getWorkPerformanceList(dto);
    }

    /*
     * 작업 실적 등록
     * */
    @Override
    @Transactional
    public Void registerPerformance(RequestWorkPerformanceDTO dto) {
        // 사원 조회
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사원이 존재하지 않습니다."));

        // 작업지시 존재 여부 확인
        WorkOrder workOrder = workOrderRepository.findById(dto.getWorkOrderId())
                .orElseThrow(() -> new NoSuchElementException("해당 작업지시가 존재하지 않습니다."));

        int totalCount = workPerformanceMapper.countTotalProcess(workOrder.getId());
        int completedCount = workPerformanceMapper.countCompletedProcess(workOrder.getId());

        // 공정 존재 여부 확인
        if (totalCount == 0) {
            throw new IllegalStateException("공정이 존재하지 않습니다. 실적 등록이 불가능합니다.");
        }

        // 모든 공정 상태 확인
        if (totalCount != completedCount) {
            throw new IllegalStateException("모든 공정이 완료되지 않았습니다. 실적 등록이 불가능합니다.");
        }

        // 작업상태 체크
        if (dto.getStatusCode() == null || !dto.getStatusCode().equals("WKS002")) {
            throw new IllegalStateException("진행중인 작업상태가 아닙니다. 실적 등록이 불가능합니다.");
        }

        // 실적 등록 처리
        WorkPerformance workPerformance = WorkPerformance.builder()
                .workOrder(workOrder)
                .fectiveQuantity(dto.getFectiveQuantity())
                .defectiveQuantity(dto.getDefectiveQuantity())
                .employee(employee)
                .endDate(dto.getEndDate())
                .build();

        // 작업지시 상태 변경
        workOrder.updateStatus("WKS003");

        workPerformanceRepository.save(workPerformance);

        return null;
    }

}
