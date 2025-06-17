package com.itwillbs.factron.service.employee;

import com.itwillbs.factron.dto.employee.RequestEmployeeNewDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.RequestEmployeeUpdateDTO;

import java.util.List;

/**
 * 사원 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface EmployeeService {

    /**
     * 검색 조건으로 사원 목록을 조회합니다.
     * 개인 또는 다수의 사원 정보를 조회할 수 있습니다.
     *
     * @param requestEmployeeSrhDTO 검색 조건을 담은 DTO
     * @return 조회된 사원 목록
     */
    List<ResponseEmployeeSrhDTO> getEmployees(RequestEmployeeSrhDTO requestEmployeeSrhDTO);

    /**
     * 사원의 개인정보를 수정합니다.
     * 권한에 따라 수정 가능한 정보가 다를 수 있습니다.
     * 재직 상태가 'N'으로 변경될 경우 권한 정보도 함께 업데이트됩니다.
     *
     * @param reqEmployeeDTO 수정할 사원 정보를 담은 DTO
     * @throws EntityNotFoundException 사원이 존재하지 않는 경우
     * @throws IllegalArgumentException 이메일 또는 전화번호가 이미 존재하는 경우
     */
    void updateEmployee(RequestEmployeeUpdateDTO reqEmployeeDTO);

    /**
     * 새로운 사원을 추가합니다.
     * 사원 정보와 함께 권한 정보도 생성됩니다.
     *
     * @param reqEmployeeNewDTO 추가할 사원 정보를 담은 DTO
     * @throws IllegalArgumentException 이메일 또는 전화번호가 이미 존재하는 경우
     */
    void addNewEmployee(RequestEmployeeNewDTO reqEmployeeNewDTO);

}


