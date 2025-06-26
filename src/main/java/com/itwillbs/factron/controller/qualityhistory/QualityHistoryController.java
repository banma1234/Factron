package com.itwillbs.factron.controller.qualityhistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class QualityHistoryController {

    @GetMapping("/quality/history")
    public String qualityHistory() {
        return "/qualityhistory/quality_history"; // quality_history.html
    }
}
