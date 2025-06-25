package com.itwillbs.factron.controller.production;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrdctPlanController {

    /*
     * 생산계획 목록 페이지
     */
    @GetMapping("/production")
    public String prdctPlan() {
        return "production/prdctPlan";
    }

    /*
     * 생산계획 등록 폼 페이지
     */
    @GetMapping("/production/save")
    public String prdctPlanForm() {
        return "production/prdctPlan-form";
    }

}
