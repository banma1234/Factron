package com.itwillbs.factron.controller.work;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkController {

    /*
     * 근무 목록 페이지
     */
    @GetMapping("/work")
    public String work() {
        return "work/work";
    }

    /*
     * 근무 등록 폼 페이지
     */
    @GetMapping("/work/save")
    public String workForm() {
        return "work/workDtl";
    }
}
