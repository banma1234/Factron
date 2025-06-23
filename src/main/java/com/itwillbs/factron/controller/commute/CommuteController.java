package com.itwillbs.factron.controller.commute;

import com.itwillbs.factron.service.commute.CommuteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/commute")
@RequiredArgsConstructor
public class CommuteController {

    private final CommuteService commuteService;

    // 출퇴근 기록 조회 페이지
    @GetMapping()
    public String commute_history(Model model) {

        // 실제 로그인 정보에서 사번을 가져와야 함 (예시: SecurityContext 등)
        String empId = "25060001"; // TODO: 실제 로그인 정보로 대체

        // 오늘 출퇴근 상태 조회
        String commuteStatus = commuteService.getTodayCommuteStatus(empId);

        model.addAttribute("commuteStatus", commuteStatus);

        return "commute/commute_history"; // commute_history.html
    }
}
