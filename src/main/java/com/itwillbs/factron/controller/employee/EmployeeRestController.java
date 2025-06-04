package com.itwillbs.factron.controller.employee;

import com.itwillbs.factron.dto.employee.EmployeeDTO;
import com.itwillbs.factron.dto.employee.EmployeeSearchDTO;
import com.itwillbs.factron.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@Log4j2
public class employeeRestController {

    private final EmployeeService employeeService;

    @GetMapping("")
    public List<EmployeeDTO> getEmployees() {
        EmployeeSearchDTO employeeSearchDTO = new EmployeeSearchDTO();
        log.info("RestContorller: type check" + employeeSearchDTO.toString());
        List<EmployeeDTO> employeeList = employeeService.getAllEmployees(employeeSearchDTO);

        return null;
    }
}
