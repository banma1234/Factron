package com.itwillbs.factron.mapper.employee;

import com.itwillbs.factron.dto.employee.EmployeeDTO;
import com.itwillbs.factron.dto.employee.EmployeeSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<EmployeeDTO> getEmployeeList(EmployeeSearchDTO employeeSearchDTO);
}
