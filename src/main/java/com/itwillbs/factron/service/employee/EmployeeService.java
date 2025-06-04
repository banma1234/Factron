package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.EmployeeRequestDTO;
import com.itwillbs.factron.dto.employee.EmployeeResponseDTO;
import com.itwillbs.factron.mapper.employee.EmployeeMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    // 검색 조건으로 사원 조회
    public List<EmployeeResponseDTO> getEmployees(EmployeeRequestDTO employeeRequestDTO){
        log.info("EmployeeService employeeReqDTO Before valid: " + employeeRequestDTO);
        employeeRequestDTO.setDept(this.validateCode(employeeRequestDTO.getDept()));
        employeeRequestDTO.setPosition(this.validateCode(employeeRequestDTO.getPosition()));
        employeeRequestDTO.setEmpIsActive(this.validActive(employeeRequestDTO.getEmpIsActive()));
        employeeRequestDTO.setNameOrId(this.safeTrim(employeeRequestDTO.getNameOrId()));
        log.info("EmployeeService employeeReqDTO After valid: " + employeeRequestDTO);
        return employeeMapper.getEmployeeList(employeeRequestDTO);
    }

    // 공백 제거
    private String safeTrim(String input) {
        return (input == null) ? "" : input.trim();
    }

    // 재직여부 확인
    private String validActive(String str){
        String trimmed = safeTrim(str);
        if (trimmed.equals("")) return trimmed;
        return ((trimmed.equals("y")  || trimmed.equals("n")) ) ?  trimmed: "";
    }

    // Code 유효성 검사
    private String validateCode(String code) {
        String trimmed = safeTrim(code);
        if (trimmed.matches("^[A-Z]{3}\\d{3}$")) {
            return trimmed;
        }
        return "";
    }


}
