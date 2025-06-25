package com.itwillbs.factron.controller.quality;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QualityInspectionController {
    @GetMapping("/quality")
    public String quality() {
        return "/quality/quality-inspection";
    }
}
