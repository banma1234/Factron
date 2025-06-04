package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.mapper.employee.EmployeeMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    /**
     * 검색 조건으로 사원 조회
     * @param requestEmployeeSrhDTO {@link RequestEmployeeSrhDTO}
     * @return List<ResponseEmployeeSrhDTO>
     */
    @Override
    public List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO){
//        log.info("EmployeeService employeeReqDTO Before valid: " + employeeRequestDTO);
        requestEmployeeSrhDTO.setDept(this.validateCode(requestEmployeeSrhDTO.getDept()));
        requestEmployeeSrhDTO.setPosition(this.validateCode(requestEmployeeSrhDTO.getPosition()));
        requestEmployeeSrhDTO.setEmpIsActive(this.validActive(requestEmployeeSrhDTO.getEmpIsActive()));
        requestEmployeeSrhDTO.setNameOrId(this.safeTrim(requestEmployeeSrhDTO.getNameOrId()));
//        log.info("EmployeeService employeeReqDTO After valid: " + employeeRequestDTO);
        return employeeMapper.getEmployeeList(requestEmployeeSrhDTO);
    }

    /**
     * 사원 정보 수정
     * @param reqEmployeeDTO {@link RequestEmployeeUpdateDTO}
     * @return Void
     */
    @Override
    @Transactional
    public Void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO) {
        Employee emp = employeeRepository.findById(reqEmployeeDTO.getEmpId()).orElseThrow(
                ()-> new EntityNotFoundException("사원번호: " + reqEmployeeDTO.getEmpId() + " 조회 결과가 없습니다.")
        );
        // RequestEmployeeUpdateDTO Validation 확인
        if (reqEmployeeDTO.getEmpName() == null || reqEmployeeDTO.getEmpName().isEmpty())
            reqEmployeeDTO.setEmpName(emp.getName());
        if (reqEmployeeDTO.getResidentRegistrationNumber() == null || reqEmployeeDTO.getResidentRegistrationNumber().isEmpty())
            reqEmployeeDTO.setResidentRegistrationNumber(emp.getBirth() + "-" + emp.getRrnBack());
        if(reqEmployeeDTO.getGender() == null || reqEmployeeDTO.getGender().isEmpty())
            reqEmployeeDTO.setGender(emp.getGender());
        if(reqEmployeeDTO.getEmail() == null || reqEmployeeDTO.getEmail().isEmpty())
            reqEmployeeDTO.setEmail(emp.getEmail());
        if(reqEmployeeDTO.getAddress() == null || reqEmployeeDTO.getAddress().isEmpty())
            reqEmployeeDTO.setAddress(emp.getAddress());
        if(reqEmployeeDTO.getEmpIsActive() == null || reqEmployeeDTO.getEmpIsActive().isEmpty())
            reqEmployeeDTO.setEmpIsActive("Y");
        reqEmployeeDTO.getEmpIsActive().toUpperCase();
        if(reqEmployeeDTO.getEmployeCode() == null || reqEmployeeDTO.getEmployeCode().isEmpty())
            reqEmployeeDTO.setEmployeCode(emp.getEmployCode());
        // 권한 확인!
        if(false)
            emp.updateTranfEmployeeInfo(reqEmployeeDTO);
        emp.updateNormEmployeeInfo(reqEmployeeDTO);
        return null;
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
