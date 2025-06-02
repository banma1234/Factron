package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.EmployeeDTO;
import com.itwillbs.factron.dto.employee.EmployeeSearchDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.mapper.employee.EmployeeMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;


    public List<EmployeeDTO> getAllEmployees(EmployeeSearchDTO employeeSearchDTO){
        List<EmployeeDTO> allEmployees = employeeMapper.getEmployeeList(employeeSearchDTO);

        return allEmployees;
    }


}
