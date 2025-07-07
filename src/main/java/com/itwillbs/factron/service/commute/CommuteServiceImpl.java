package com.itwillbs.factron.service.commute;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.commute.RequestCommuteDTO;
import com.itwillbs.factron.dto.commute.ResponseCommuteDTO;
import com.itwillbs.factron.entity.CommuteHistory;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.WorkHistory;
import com.itwillbs.factron.mapper.commute.CommuteMapper;
import com.itwillbs.factron.repository.commute.CommuteRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.work.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommuteServiceImpl implements CommuteService {

    private final CommuteRepository commuteRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkRepository workRepository;

    private final CommuteMapper commuteMapper;

    private final AuthorizationChecker authorizationChecker;

    /**
     * 출근 처리 메소드
     */
    @Override
    @Transactional
    public void commuteIn() {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

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
     */
    @Override
    @Transactional
    public void commuteOut() {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

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

        // 5. 퇴근 시간 설정
        commuteHistory.changeCommuteOut(LocalDateTime.now());

        // 6. 근무 테이블에 일반 근무로 저장할 근무 데이터 생성
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        WorkHistory workHistory = WorkHistory.builder()
                .workDate(LocalDate.now())
                .startTime(commuteHistory.getCommuteIn().format(timeFormatter))
                .endTime(commuteHistory.getCommuteOut().format(timeFormatter))
                .workCode("WRK001") // 일반 근무 코드
                .employee(employee)
                .build();

        // 7. 근무 기록 저장
        workRepository.save(workHistory);
    }

    /**
     * 출근 기록 조회 메소드
     * @param requestDto 조회 요청 DTO
     * @return 출퇴근 기록 DTO 리스트
     */
    @Override
    public List<ResponseCommuteDTO> getCommuteHistories(RequestCommuteDTO requestDto) {

        // 1. 입력 값이 null 또는 빈 문자열인 경우 null로 변환하는 메소드로
        String nameOrId = safeTrim(requestDto.getNameOrId());
        String deptCode = safeTrim(requestDto.getDept());
        String startDate = safeTrim(requestDto.getStartDate());
        String endDate = safeTrim(requestDto.getEndDate());

        // 2. 날짜 형식 유효성 검사
        validateDateFormat(startDate, "startDate");
        validateDateFormat(endDate, "endDate");

        // 3. CommuteRequestDto 객체 생성
        RequestCommuteDTO requestCommuteDto = RequestCommuteDTO.builder()
                .nameOrId(nameOrId)
                .dept(deptCode)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // 4. CommuteMapper를 사용하여 출퇴근 기록 조회
        return commuteMapper.selectCommuteHistories(requestCommuteDto);
    }

    /**
     * 오늘 출근 상태 조회 메소드
     * @return 출근 상태 (IN, DONE, NONE)
     */
    @Override
    public String getTodayCommuteStatus() {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        // 1. 사원 정보 조회 (예: employeeRepository.findById)
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사원이 없습니다."));

        // 2. 오늘 날짜의 시작과 끝 계산
        LocalDate date = LocalDate.now(); // 현재 날짜
        LocalDateTime startOfDay = date.atStartOfDay(); // 오늘 시작 시간 (00:00:00)
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1); // 오늘 끝 시간 (23:59:59.999999999)

        // 3. 오늘 출근 기록 조회
        Optional<CommuteHistory> optionalHistory = commuteRepository
                .findByEmployeeAndCommuteInBetween(employee, startOfDay, endOfDay);

        if (optionalHistory.isEmpty()) {

            return "NONE"; // 출근 기록 없음
        }

        CommuteHistory history = optionalHistory.get();

        return (history.getCommuteOut() == null) ? "IN" : "DONE";
    }

    /**
     * 입력 값의 공백을 제거하고 null 처리하는 메소드
     * @param value 입력 값
     * @return 공백 제거된 값 또는 null
     */
    private String safeTrim(String value) {

        // 입력 값이 null 또는 빈 문자열인 경우 null로 변환
        return (value != null && !value.isEmpty()) ? value.trim() : null;
    }

    /**
     * 날짜 형식 유효성 검사 메소드
     * @param value 입력 값
     * @param fieldName 필드 이름 (오류 메시지에 사용)
     */
    private void validateDateFormat(String value, String fieldName) {

        // 입력 값이 null 또는 빈 문자열인 경우 아무 작업도 하지 않음
        if (value != null && !value.isEmpty() && !value.matches("\\d{4}-\\d{2}-\\d{2}")) {

            // 날짜 형식이 잘못된 경우 예외 발생
            throw new IllegalArgumentException(fieldName + "는 yyyy-MM-dd 형식이어야 합니다.");
        }
    }
}
