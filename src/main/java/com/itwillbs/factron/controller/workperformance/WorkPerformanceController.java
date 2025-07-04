package com.itwillbs.factron.controller.workperformance;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkPerformanceController {
    /*
     * 실적 목록 페이지
     */
    @GetMapping("/performance")
    public String workperformanceGrid() {
        return "workperformance/workperformance";
    }
}
