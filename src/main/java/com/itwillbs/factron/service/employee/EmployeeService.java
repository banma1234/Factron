package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;

import java.util.List;


public interface EmployeeService {

    // 검색 조건으로 사원 조회

    /**
     * 검색 조건으로 사원 목록 조회 (개인 or 다수)
     * @param requestEmployeeSrhDTO {@link RequestEmployeeSrhDTO}
     * @return List<responseEmployeeSrhDTO>
     */
    List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO);

    /**
     * 사원의 개인정보를 수정 (권한에 따라 선택 기준 다름)
     * @param reqEmployeeDTO {@link RequestEmployeeUpdateDTO}
     * @return Void
     */
    void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO);

    /**
     * 새로운 사원 추가
     * @param reqEmployeeNewDTO {@link RequestEmployeeNewDTO}
     * @return Void
     */
    void addNewEmployee(RequestEmployeeNewDTO reqEmployeeNewDTO);

}


