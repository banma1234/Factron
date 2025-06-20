package com.itwillbs.factron.controller.vacation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vacation")
public class VacationController {

    @GetMapping("")
    public String vacationGrid() {
        return "vacation/vacation";
    }


    @GetMapping("/save")
    public String showVacationApply() {
        return "vacation/vacation-form";
    }
}