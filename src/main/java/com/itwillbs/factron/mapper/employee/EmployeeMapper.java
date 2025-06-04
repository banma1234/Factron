package com.itwillbs.factron.mapper.employee;

import com.itwillbs.factron.dto.employee.ResponseEmployeeDTO;
import com.itwillbs.factron.dto.employee.EmployeeSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<ResponseEmployeeDTO> getEmployeeList(EmployeeSearchDTO employeeSearchDTO);
}
