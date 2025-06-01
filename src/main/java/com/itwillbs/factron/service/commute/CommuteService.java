package com.itwillbs.factron.service.commute;

import com.itwillbs.factron.dto.commute.CommuteResponseDto;

import java.util.List;
import java.util.Map;

public interface CommuteService {

    // 출근 기록을 저장하는 메서드
    void commuteIn(String employeeId);

    // 퇴근 기록을 저장하는 메서드
    void commuteOut(String employeeId);

    // 출근 기록을 조회하는 메서드
    List<CommuteResponseDto> getCommuteHistories(Map<String, String> params);
}
