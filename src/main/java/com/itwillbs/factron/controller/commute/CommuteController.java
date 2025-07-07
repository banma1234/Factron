package com.itwillbs.factron.controller.commute;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.service.commute.CommuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/commute")
@RequiredArgsConstructor
public class CommuteController {

    private final CommuteService commuteService;

    // 출퇴근 기록 조회 페이지
    @GetMapping()
    public String commute_history(Model model) {

        // 오늘 출퇴근 상태 조회
        String commuteStatus = commuteService.getTodayCommuteStatus();

        model.addAttribute("commuteStatus", commuteStatus);

        return "commute/commute_history"; // commute_history.html
    }
}
