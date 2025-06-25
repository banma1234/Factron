package com.itwillbs.factron.service.machine;

import com.itwillbs.factron.dto.machine.RequestAddMachineDTO;
import com.itwillbs.factron.dto.machine.RequestMachineInfoDTO;
import com.itwillbs.factron.dto.machine.RequestUpdateMachineDTO;
import com.itwillbs.factron.dto.machine.ResponseMachineInfoDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface MachineService {

    // 설비 추가
    void addMachine(RequestAddMachineDTO requestDto, Long empId);

    // 설비 수정
    void updateMachine(RequestUpdateMachineDTO requestDto, Long empId);

    // 설비 목록 조회
    List<ResponseMachineInfoDTO> getMachineList(RequestMachineInfoDTO requestDto);
}
