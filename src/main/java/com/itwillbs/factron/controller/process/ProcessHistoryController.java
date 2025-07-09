package com.itwillbs.factron.controller.process;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProcessHistoryController {
    @GetMapping("/process/history")
    public String process() {
        return "/process/process-history";
    }

    @GetMapping("/process/history/statistics")
    public String processStatistics() {
        return "/process/process-history-statistics";
    }
}
