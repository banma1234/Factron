package com.itwillbs.factron.mapper.employee;

import com.itwillbs.factron.dto.employee.EmployeeRequestDTO;
import com.itwillbs.factron.dto.employee.EmployeeResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<EmployeeResponseDTO> getEmployeeList(EmployeeRequestDTO employeeRequestDTO);
}
