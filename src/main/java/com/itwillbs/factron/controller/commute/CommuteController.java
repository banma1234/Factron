package com.itwillbs.factron.controller.commute;

import com.itwillbs.factron.service.commute.CommuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/commute")
@RequiredArgsConstructor
public class CommuteController {

    private final CommuteService commuteService;

    @GetMapping()
    public String commute_history(Model model) {

        // 실제 로그인 정보에서 사번을 가져와야 함 (예시: SecurityContext 등)
        String empId = "1"; // TODO: 실제 로그인 정보로 대체

        String today = LocalDate.now().toString(); // 오늘 날짜를 문자열로 변환

        // 오늘 출퇴근 상태 조회
        String commuteStatus = commuteService.getTodayCommuteStatus(empId, today);

        model.addAttribute("commuteStatus", commuteStatus);

        return "work/commute_history";
    }
}
