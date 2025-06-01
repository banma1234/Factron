package com.itwillbs.factron.service.commute;

import com.itwillbs.factron.dto.commute.CommuteResponseDto;
import com.itwillbs.factron.entity.CommuteHistory;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.mapper.commute.CommuteMapper;
import com.itwillbs.factron.repository.commute.CommuteRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommuteServiceImpl implements CommuteService {

    private final CommuteRepository commuteRepository;
    private final EmployeeRepository employeeRepository;

    private final CommuteMapper commuteMapper;

    /**
     * 출근 처리 메소드
     * @param employeeId 사원 ID
     */
    @Override
    @Transactional
    public void commuteIn(String employeeId) {

        log.info("출근 처리: employeeId={}", employeeId);

        Long empId = Long.parseLong(employeeId);

        // 1. 사원 조회
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사원이 존재하지 않습니다."));


        // 2. 오늘 날짜의 시작과 끝 계산
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(); // 오늘 시작 시간 (00:00:00)
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1); // 오늘 끝 시간 (23:59:59.999999999)

        // 3. 오늘 출근 기록 조회
        if (commuteRepository.findByEmployeeAndCommuteInBetween(employee, startOfDay, endOfDay).isPresent()) {
            throw new IllegalArgumentException("오늘 이미 출근 기록이 있습니다.");
        }

        // 4. 출근 기록 생성
        CommuteHistory commuteHistory = CommuteHistory.builder()
                .employee(employee) // Employee 객체 전달
                .commuteIn(LocalDateTime.now())
                .build();

        // 5. 출근 기록 저장
        commuteRepository.save(commuteHistory);
    }

    /**
     * 퇴근 처리 메소드
     * @param employeeId 사원 ID
     */
    @Override
    @Transactional
    public void commuteOut(String employeeId) {
        log.info("퇴근 처리: employeeId={}", employeeId);

        Long empId = Long.parseLong(employeeId);

        // 1. 사원 조회
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사원이 존재하지 않습니다."));

        // 2. 오늘 날짜의 시작과 끝 계산
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        // 3. 오늘 출근 기록 조회
        CommuteHistory commuteHistory = commuteRepository
                .findByEmployeeAndCommuteInBetween(employee, startOfDay, endOfDay)
                .orElseThrow(() -> new NoSuchElementException("오늘 출근 기록이 없습니다."));

        // 4. 이미 퇴근 기록이 있으면 예외
        if (commuteHistory.getCommuteOut() != null) {
            throw new IllegalArgumentException("이미 퇴근한 상태입니다.");
        }

        // 5. 퇴근 시간 저장 (퇴근 기록 업데이트)
        commuteHistory = CommuteHistory.builder()
                .id(commuteHistory.getId())
                .employee(employee)
                .commuteIn(commuteHistory.getCommuteIn())
                .commuteOut(LocalDateTime.now())
                .build();

        // 6. 퇴근 기록 저장
        commuteRepository.save(commuteHistory);
    }

    /**
     * 출근 기록 조회 메소드
     * @param params 조회 조건 파라미터
     * @return 출근 기록 리스트
     */
    @Override
    public List<CommuteResponseDto> getCommuteHistories(Map<String, String> params) {

        Map<String, Object> queryParams = new HashMap<>();

        // empId 변환 (null, 빈 문자열 체크)
        String empIdStr = params.get("empId");
        if (empIdStr != null && !empIdStr.isEmpty()) {
            try {
                Long employeeId = Long.parseLong(empIdStr.trim());
                queryParams.put("empId", employeeId);
            } catch (NumberFormatException e) {

                throw new NumberFormatException("empId는 숫자여야 합니다.");
            }
        }

        // 사원 이름
        String employeeName = params.get("name");
        if (employeeName != null && !employeeName.isEmpty()) {
            queryParams.put("empName", employeeName.trim());  // "name" → "empName"
        }

        // 부서 코드
        String departmentCode = params.get("dept");
        if (departmentCode != null && !departmentCode.isEmpty()) {
            queryParams.put("deptCode", departmentCode.trim());  // "dept" → "deptCode"
        }

        // 시작 일자
        String startDate = params.get("startDate");
        if (startDate != null && !startDate.isEmpty()) {
            if (!startDate.matches("\\d{4}-\\d{2}-\\d{2}")) {

                throw new IllegalArgumentException("startDate는 yyyy-MM-dd 형식이어야 합니다.");
            }
            queryParams.put("startDate", startDate.trim());
        }

        // 종료 일자
        String endDate = params.get("endDate");
        if (endDate != null && !endDate.isEmpty()) {
            if (!endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {

                throw new IllegalArgumentException("endDate는 yyyy-MM-dd 형식이어야 합니다.");
            }
            queryParams.put("endDate", endDate.trim());
        }

        // 이제 마이바티스 매퍼 호출
        return commuteMapper.selectCommuteHistories(queryParams);
    }
}
