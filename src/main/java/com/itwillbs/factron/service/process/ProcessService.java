package com.itwillbs.factron.service.process;

import com.itwillbs.factron.dto.process.RequestAddProcessDTO;
import com.itwillbs.factron.dto.process.RequestProcessInfoDTO;
import com.itwillbs.factron.dto.process.RequestUpdateProcessDTO;
import com.itwillbs.factron.dto.process.ResponseProcessInfoDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface ProcessService {

    // 공정 추가
    void addProcess(RequestAddProcessDTO requestDto);

    // 공정 수정
    void updateProcess(RequestUpdateProcessDTO requestDto);

    // 공정 목록 조회
    List<ResponseProcessInfoDTO> getProcessList(RequestProcessInfoDTO requestDto);
}
