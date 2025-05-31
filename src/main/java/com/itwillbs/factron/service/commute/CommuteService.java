package com.itwillbs.factron.service.commute;

public interface CommuteService {

    // 출근 기록을 저장하는 메서드
    void commuteIn(String employeeId);

    // 퇴근 기록을 저장하는 메서드
    void commuteOut(String employeeId);
}
