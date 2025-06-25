package com.itwillbs.factron.mapper.machine;

import com.itwillbs.factron.dto.machine.RequestMachineInfoDTO;
import com.itwillbs.factron.dto.machine.ResponseMachineInfoDTO;
import com.itwillbs.factron.dto.process.RequestProcessInfoDTO;
import com.itwillbs.factron.dto.process.ResponseProcessInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MachineMapper {

    // 공정 목록 조회
    List<ResponseMachineInfoDTO> selectMachines(RequestMachineInfoDTO requestDto);
}
