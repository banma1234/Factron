package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.ResponseEmployeeDTO;
import com.itwillbs.factron.dto.employee.EmployeeSearchDTO;
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
    public List<ResponseEmployeeDTO> getEmployees(EmployeeSearchDTO employeeSearchDTO){
        List<ResponseEmployeeDTO> employees = employeeMapper.getEmployeeList(employeeSearchDTO);
        return employees;
    }


}
