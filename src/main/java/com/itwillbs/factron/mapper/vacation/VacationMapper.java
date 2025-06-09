package com.itwillbs.factron.mapper.vacation;


import com.itwillbs.factron.dto.vacation.VacationResponseDTO;
import com.itwillbs.factron.entity.VacationHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


@Mapper
public interface VacationMapper {

    List<VacationResponseDTO> findMyVacations(
            @Param("empId") Long empId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );


    Long emplId = 10001L;
    Long selectRandomHrManagerId(@Param("empId") Long empId);
//    Long selectRandomHrManagerId();
    void insertVacation(VacationHistory vacation);

    boolean VacationCheck(@Param("empId") Long empId,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

}
