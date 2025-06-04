package com.itwillbs.factron.controller.employee;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.employee.EmployeeRequestDTO;
import com.itwillbs.factron.dto.employee.EmployeeResponseDTO;
import com.itwillbs.factron.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@Log4j2
public class EmployeeRestController {

    private final EmployeeService employeeService;

    // 사원 리스트 호출 API
    @GetMapping("")
    public ResponseDTO<List<EmployeeResponseDTO>> getEmployees(@ModelAttribute EmployeeRequestDTO reqEmployeeDTO) {
        // 검색된 사원 목록 반환
        return ResponseDTO.success(employeeService.getEmployees(reqEmployeeDTO));
    }
}
