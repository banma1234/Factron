package com.itwillbs.factron.controller.employee;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import com.itwillbs.factron.service.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@Log4j2
public class EmployeeRestController {

    private final EmployeeService employeeService;

    /**
     * 사원 리스트 조회 API
     * @param reqEmpDto {@link RequestEmployeeSrhDTO}
     * @return ResponseDTO<List<ResponseEmployeeSrhDTO>> 조회된 사원 목록
     */
    @GetMapping("")
    public ResponseDTO<List<ResponseEmployeeSrhDTO>> getEmployees(@ModelAttribute RequestEmployeeSrhDTO reqEmpDto) {
        try {
            List<ResponseEmployeeSrhDTO> employees = employeeService.getEmployees(reqEmpDto);
            return ResponseDTO.success(employees);
        } catch (IllegalArgumentException e) {
            return ResponseDTO.fail(800, e.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, e.getMessage(), null);
        }
    }

    /**
     * 사원 정보 수정 API
     * @param requestEmployeeUpdateDTO {@link RequestEmployeeUpdateDTO}
     * @return ResponseDTO<List<String>> validation errors or success message
     */
    @PutMapping("")
    public ResponseDTO<Void> updateEmployee(@Valid @RequestBody RequestEmployeeUpdateDTO requestEmployeeUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            return ResponseDTO.fail(400, errors.getFirst(), null);
        }

        try {
            employeeService.updateEmployee(requestEmployeeUpdateDTO);
            return ResponseDTO.success("사원 정보가 저장되었습니다.", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
    }

    /**
     * 사원 추가
     * @param reqEmployeeNewDTO {@link RequestEmployeeNewDTO}
     * @return ResponseDTO<Void>
     */
    @PostMapping("")
    public ResponseDTO<Void> addEmployee(@Valid @RequestBody RequestEmployeeNewDTO reqEmployeeNewDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            return ResponseDTO.fail(400, errors.getFirst(), null);
        }

        try{
            employeeService.addNewEmployee(reqEmployeeNewDTO);
            return ResponseDTO.success("사원이 추가되었습니다.", null);
        }catch(IllegalArgumentException e){
            return ResponseDTO.fail(800, e.getMessage(), null);
        }
        catch (Exception e) {
            return  ResponseDTO.fail(801, e.getMessage(), null);
        }
    }
}
