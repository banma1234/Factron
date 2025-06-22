package com.itwillbs.factron.service.line;

import com.itwillbs.factron.dto.line.RequestAddLineDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.RequestUpdateLineDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface lineService {

    // 라인 목록 조회
    List<ResponseLineInfoDTO> getLineList(RequestLineInfoDTO requestDto);

    // 라인 추가
    void addLine(RequestAddLineDTO requestDto, Long empId);

    // 라인 수정
    void updateLine(RequestUpdateLineDTO requestDto, Long empId);
}
