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
    private final AuthorizationChecker authorizationChecker;

    // 출퇴근 기록 조회 페이지
    @GetMapping()
    public String commute_history(Model model) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        String empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 오늘 출퇴근 상태 조회
        String commuteStatus = commuteService.getTodayCommuteStatus(empId);

        model.addAttribute("commuteStatus", commuteStatus);

        return "commute/commute_history"; // commute_history.html
    }
}
