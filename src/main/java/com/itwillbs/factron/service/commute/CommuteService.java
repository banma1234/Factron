package com.itwillbs.factron.service.commute;

import com.itwillbs.factron.dto.commute.RequestCommuteDTO;
import com.itwillbs.factron.dto.commute.ResponseCommuteDTO;

import java.util.List;

public interface CommuteService {

    // 출근 기록을 저장하는 메서드
    void commuteIn(String employeeId);

    // 퇴근 기록을 저장하는 메서드
    void commuteOut(String employeeId);

    // 출근 기록을 조회하는 메서드
    List<ResponseCommuteDTO> getCommuteHistories(RequestCommuteDTO requestCommuteDto);

    // 특정 사원의 출근 기록을 조회하는 메서드 (출퇴근 버튼 활성화 여부를 판단하기 위해 사용)
    String getTodayCommuteStatus(String empId, String todayDate);
}
