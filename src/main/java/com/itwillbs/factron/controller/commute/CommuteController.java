package com.itwillbs.factron.controller.commute;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/commute")
public class CommuteController {

    @GetMapping()
    public String commute_history() {

        return "work/commute_history";
    }
}
