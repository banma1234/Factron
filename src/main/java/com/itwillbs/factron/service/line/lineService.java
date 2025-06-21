package com.itwillbs.factron.service.line;

import com.itwillbs.factron.dto.line.RequestAddLineDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;

import java.util.List;

public interface lineService {

    // 라인 목록 조회
    List<ResponseLineInfoDTO> getLineList(RequestLineInfoDTO requestDto);

    void addLine(RequestAddLineDTO requestDto, Long empId);
}
