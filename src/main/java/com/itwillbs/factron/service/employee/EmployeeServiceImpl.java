package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.common.component.AESUtil;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final IntergratAuthRepository intergratAuthRepository;
    private final EmployeeMapper employeeMapper;
    private final AESUtil aesUtil;

    /**
     * 검색 조건으로 사원 조회
     * @param requestEmployeeSrhDTO {@link RequestEmployeeSrhDTO}
     * @return List<ResponseEmployeeSrhDTO>
     */
    @Override
    public List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO){
        return employeeMapper.getEmployeeList(requestEmployeeSrhDTO)
                                    .stream()
                                    .map(dto -> {
                                        dto.setRrnBack(aesUtil.decrypt(dto.getRrnBack()));
                                        dto.setJoinedDate(dateParse(dto.getJoinedDate()));
                                        if(dto.getQuitDate() != null){
                                            dto.setQuitDate(dateParse(dto.getQuitDate()));
                                        }
                                        return dto;
                                    })
                                    .collect(Collectors.toList());
    }

    /**
     * 사원 정보 수정
     * @param reqEmployeeDTO {@link RequestEmployeeUpdateDTO}
     * @return Void
     */
    @Override
    @Transactional
    public void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO) {
        Employee emp = employeeRepository.findById(reqEmployeeDTO.getEmpId()).orElseThrow(
                ()-> new EntityNotFoundException("사원번호: " + reqEmployeeDTO.getEmpId() + " 조회 결과가 없습니다.")
        );
        // RequestEmployeeUpdateDTO Validation 확인
        if (reqEmployeeDTO.getEmpName() == null || reqEmployeeDTO.getEmpName().isEmpty())
            reqEmployeeDTO.setEmpName(emp.getName());

        if (reqEmployeeDTO.getRrnBack() == null || reqEmployeeDTO.getRrnBack().isEmpty()){
            reqEmployeeDTO.setRrnBack(emp.getRrnBack());
        }else{
            reqEmployeeDTO.setRrnBack(aesUtil.encrypt(reqEmployeeDTO.getRrnBack()));
        }

        if(reqEmployeeDTO.getGender() == null || reqEmployeeDTO.getGender().isEmpty())
            reqEmployeeDTO.setGender(emp.getGender());

        if(reqEmployeeDTO.getEmail() == null || reqEmployeeDTO.getEmail().isEmpty())
            reqEmployeeDTO.setEmail(emp.getEmail());

        if(reqEmployeeDTO.getAddress() == null || reqEmployeeDTO.getAddress().isEmpty())
            reqEmployeeDTO.setAddress(emp.getAddress());

        if(reqEmployeeDTO.getEmpIsActive() == null || reqEmployeeDTO.getEmpIsActive().isEmpty()
                || !validActive(reqEmployeeDTO.getEmpIsActive().toUpperCase()).isEmpty())
            reqEmployeeDTO.setEmpIsActive("Y");

        if(reqEmployeeDTO.getEmployeCode() == null || reqEmployeeDTO.getEmployeCode().isEmpty())
            reqEmployeeDTO.setEmployeCode(emp.getEmployCode());

        // 권한 확인!
        if(false)
            emp.updateTranfEmployeeInfo(reqEmployeeDTO);
        emp.updateNormEmployeeInfo(reqEmployeeDTO);
    }

    /**
     * 새로운 사원 추가
     * @param reqEmployeeNewDTO {@link RequestEmployeeNewDTO}
     * @return Void
     */
    @Override
    @Transactional
    public void addNewEmployee(RequestEmployeeNewDTO reqEmployeeNewDTO){

        if(employeeRepository.existsByEmail(reqEmployeeNewDTO.getEmail())){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if(employeeRepository.existsByPhone(reqEmployeeNewDTO.getPhone())){
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
                ()-> new EntityNotFoundException("It does not exist")
        );



        IntergratAuth newIntergratAuth = reqEmployeeNewDTO.toIntergratAuth(newEmp);
        intergratAuthRepository.save(newIntergratAuth);
    }

    // 공백 제거
    private String safeTrim(String input) {
        return (input == null) ? "" : input.trim();
    }

    // 재직여부 확인
    private String validActive(String str){
        String trimmed = safeTrim(str);
        if (trimmed.isEmpty()) return trimmed;
        return ((trimmed.equals("Y")  || trimmed.equals("N")) ) ?  trimmed: "";
    }

    // Code 유효성 검사
    private String validateCode(String code) {
        String trimmed = safeTrim(code);
        if (trimmed.matches("^[A-Z]{3}\\d{3}$")) {
            return trimmed;
        }
        return "";
    }

    private String dateParse(String dateTime) {
        try{
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
            throw new IllegalArgumentException("Invalid date format: " + dateTime, e);
        }

       throw new RuntimeException("Invalid date format: " + dateTime);
    }


    public Long generateEmployeeId() {
        LocalDate now = LocalDate.now();
        String yy = String.format("%02d", now.getYear() % 100); // 올해 2자리
        String mm = String.format("%02d", now.getMonthValue()); // 월 2자리

        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        long countThisMonth = employeeRepository.countByYearMonth(yearMonth);
        String nnn = String.format("%04d", countThisMonth + 1); // 3자리로 채움

        String idStr = yy + mm + nnn;

        return Long.parseLong(idStr);
    }
}
