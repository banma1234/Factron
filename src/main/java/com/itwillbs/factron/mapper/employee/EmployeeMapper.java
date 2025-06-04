package com.itwillbs.factron.mapper.employee;

import com.itwillbs.factron.dto.employee.RequestEmployeeSrhDTO;
import com.itwillbs.factron.dto.employee.ResponseEmployeeSrhDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<ResponseEmployeeSrhDTO> getEmployeeList(RequestEmployeeSrhDTO requestEmployeeSrhDTO);
}
