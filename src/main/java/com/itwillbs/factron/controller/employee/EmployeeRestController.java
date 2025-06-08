package com.itwillbs.factron.controller.employee;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;
import com.itwillbs.factron.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
     * 사원 조회
     * @param reqEmployeeDTO {@link RequestEmployeeSrhDTO}
     * @return ResponseDTO<List<ResponseEmployeeSrhDTO>> 조회된 사원 목록
     */
    // 사원 리스트 호출 API
    @GetMapping("")
    public ResponseDTO<List<ResponseEmployeeSrhDTO>> getEmployees(@ModelAttribute RequestEmployeeSrhDTO reqEmployeeDTO) {
//        log.info("EmpRestController getEmployees reqEmployeeDTO: "+reqEmployeeDTO);
        try {
            List<ResponseEmployeeSrhDTO> employees = employeeService.getEmployees(reqEmployeeDTO);

            for (ResponseEmployeeSrhDTO dto : employees) {
                log.info("Controller 확인 - empId: {}, rrnBack: {}", dto.getEmpId(), dto.getRrnBack());
            }
            return ResponseDTO.success(employees);
        } catch (IllegalArgumentException e) {
            return ResponseDTO.fail(400, e.getMessage(), new ArrayList<>());
        } catch (Exception e) {
            return ResponseDTO.fail(500, "서버 오류가 발생했습니다.", new ArrayList<>());
        }
    }

    /**
     * 사원 정보 수정
     * @param requestEmployeeUpdateDTO {@link RequestEmployeeUpdateDTO}
     * @return ResponseDTO<Void>
     */
    @PutMapping("")
    public ResponseDTO<Void> updateEmployee(@RequestBody RequestEmployeeUpdateDTO requestEmployeeUpdateDTO) {
//        log.info("EmployeeRestController updateEmployee input requestEmployeeUpdateDTO:" + requestEmployeeUpdateDTO);
        try{
            return ResponseDTO.success("사원 정보가 저장되었습니다.",employeeService.updateEmployee(requestEmployeeUpdateDTO));
        }catch (Exception e) {
            return ResponseDTO.fail(500, "서버 오류가 발생했습니다.", null);
        }
    }

    /**
     * 사원 추가
     * @param reqEmployeeNewDTO {@link RequestEmployeeNewDTO}
     * @return ResponseDTO<Void>
     */
    @PostMapping("")
    public ResponseDTO<Void> addEmployee(@RequestBody RequestEmployeeNewDTO reqEmployeeNewDTO) {
        log.info("EmployeeRestController addEmployee input reqEmployeeNewDTO:" + reqEmployeeNewDTO.toString());
        try{
            return ResponseDTO.success("사원이 추가되었습니다.", employeeService.addNewEmployee(reqEmployeeNewDTO));
        }catch (Exception e) {
            return  ResponseDTO.fail(500, "서버 오류가 발생했습니다.", null);
        }
    }
}
