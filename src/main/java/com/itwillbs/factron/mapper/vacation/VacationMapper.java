package com.itwillbs.factron.mapper.vacation;


import com.itwillbs.factron.dto.vacation.VacationRequestDTO;
import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.entity.VacationHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


@Mapper
public interface VacationMapper {

    List<VacationResponseDTO> getVacations(VacationRequestDTO vacationRequestDTO);

    Integer VacationCheck(VacationRequestDTO dto);

}
