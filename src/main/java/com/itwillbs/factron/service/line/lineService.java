package com.itwillbs.factron.service.line;

import com.itwillbs.factron.dto.line.*;

import java.util.List;

public interface lineService {

    // 라인 목록 조회
    List<ResponseLineInfoDTO> getLineList(RequestLineInfoDTO requestDto);

    // 라인 추가
    void addLine(RequestAddLineDTO requestDto, Long empId);

    // 라인 수정
    void updateLine(RequestUpdateLineDTO requestDto, Long empId);

    // 라인에 여러 공정 연결
    void connectProcessesToLine(RequestConnectProcessesToLineDTO requestDto, Long empId);

    // 라인에 여러 공정 연결 해제
    void disconnectProcessesFromLine(RequestDisconnectProcessesFromLineDTO requestDto, Long empId);
}
