package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.common.component.AESUtil;
import com.itwillbs.factron.common.component.PasswordService;
import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import com.itwillbs.factron.mapper.employee.EmployeeMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {
    private static final boolean HAS_ADMIN_PERMISSION = true; // TODO: 실제 권한 체크 로직으로 대체 필요

    private final EmployeeRepository employeeRepository;
    private final IntergratAuthRepository intergratAuthRepository;
    private final EmployeeMapper employeeMapper;
    private final AESUtil aesUtil;
    private final PasswordService passwordService;

    @Override
    public List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO) {
        return employeeMapper.getEmployeeList(requestEmployeeSrhDTO)
                .stream()
                .map(dto -> {
                    dto.setRrnBack(aesUtil.decrypt(dto.getRrnBack()));
                    dto.setJoinedDate(dateParse(dto.getJoinedDate()));
                    if(dto.getQuitDate() != null) {
                        dto.setQuitDate(dateParse(dto.getQuitDate()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO) {
        Employee emp = employeeRepository.findById(reqEmployeeDTO.getEmpId()).orElseThrow(
                ()-> new EntityNotFoundException("사원번호: " + reqEmployeeDTO.getEmpId() + " 조회 결과가 없습니다.")
        );

        // 자신의 이메일/전화번호가 아닌 다른 사원의 것과 중복 체크
        if(!emp.getEmail().equals(reqEmployeeDTO.getEmail()) && 
           employeeRepository.existsByEmail(reqEmployeeDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(!emp.getPhone().equals(reqEmployeeDTO.getPhone()) && 
           employeeRepository.existsByPhone(reqEmployeeDTO.getPhone())) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        // 주민번호 뒷자리 암호화
        reqEmployeeDTO.setRrnBack(aesUtil.encrypt(reqEmployeeDTO.getRrnBack()));

        // 권한 확인
        if(HAS_ADMIN_PERMISSION) {
            // 재직상태 변경 확인
            if("N".equals(reqEmployeeDTO.getEmpIsActive())) {
                IntergratAuth intergratAuth = intergratAuthRepository.findByEmployee(emp)
                        .orElseThrow(() -> new EntityNotFoundException("해당 사원의 권한 정보가 없습니다."));
                intergratAuth.updateIsActive(reqEmployeeDTO.getEmpIsActive());
            }
            emp.updateTranfEmployeeInfo(reqEmployeeDTO);
        }
        emp.updateNormEmployeeInfo(reqEmployeeDTO);
    }

    @Override
    @Transactional
    public void addNewEmployee(RequestEmployeeNewDTO reqEmployeeNewDTO) {
        if(employeeRepository.existsByEmail(reqEmployeeNewDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(employeeRepository.existsByPhone(reqEmployeeNewDTO.getPhone())) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        // 새 아이디 생성
        Long newId = generateEmployeeId();

        // 주민번호 뒷자리 암호화
        reqEmployeeNewDTO.setRrnBack(aesUtil.encrypt(reqEmployeeNewDTO.getRrnBack()));

        // 생성자 id 수정하기
        Employee employee = reqEmployeeNewDTO.toEntity(newId, 1000L);
        employeeRepository.save(employee);

        Employee newEmp = employeeRepository.findById(newId).orElseThrow(
                ()-> new EntityNotFoundException("존재하지 않는 회원번호입니다.")
        );

        // 사원 권한 설정
        String newAuthCode = switch (newEmp.getDeptCode()) {
            case "DEP001" -> "ATH002";           // 인사
            case "DEP002" -> {
                if("POS006".equals(newEmp.getPositionCode()) || "POS007".equals(newEmp.getPositionCode()))
                    yield "ATH003";
                else
                    yield "ATH001";
            }        // 관리자
            case "DEP005" ->  "ATH004";    // 재무
            case "DEP006" -> {
                if("POS005".equals(newEmp.getPositionCode()))
                    yield "ATH006";
                else
                    yield "ATH001";
            }   // 생산
            default -> "ATH001";
        };

        // 비밀번호 암호화 (전화번호를 기본 비밀번호로 사용)
        String encodedPassword = passwordService.generateDefaultPassword(reqEmployeeNewDTO.getPhone());
        
        IntergratAuth newIntergratAuth = reqEmployeeNewDTO.toIntergratAuth(newEmp, newAuthCode);
        newIntergratAuth.updatePassword(encodedPassword); // 암호화된 비밀번호 설정

        intergratAuthRepository.save(newIntergratAuth);
    }

    // DB에서 받아온 날짜 형식을 YYYY-MM-DD 형식으로 수정합니다
    private String dateParse(String dateTime) {
        try {
            if (dateTime.contains(" ")) {
                // "2025-06-13 00:00:00" 형태
                LocalDateTime parsed = LocalDateTime.parse(dateTime.replace(' ', 'T'));
                return parsed.toLocalDate().toString();
            } else if (dateTime.contains("T")) {
                // "2025-06-13T00:00:00" 형태
                LocalDateTime parsed = LocalDateTime.parse(dateTime);
                return parsed.toLocalDate().toString();
            } else if (dateTime.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // "2025-06-13" 형태
                return dateTime;
            }
        } catch (Exception e) {
            // 포맷 에러 처리
            throw new IllegalArgumentException("올바르지 않은 날짜 형식입니다: " + dateTime, e);
        }

        throw new RuntimeException("올바르지 않은 날짜 형식입니다: " + dateTime);
    }

    // 새로운 사번 번호를 생성합니다.
    private Long generateEmployeeId() {
        LocalDate now = LocalDate.now();
        String yy = String.format("%02d", now.getYear() % 100); // 올해 2자리
        String mm = String.format("%02d", now.getMonthValue()); // 월 2자리

        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        long countThisMonth = employeeRepository.countByYearMonth(yearMonth);
        String nnn = String.format("%04d", countThisMonth + 1); // 4자리로 채움

        String idStr = yy + mm + nnn;

        return Long.parseLong(idStr);
    }
}
