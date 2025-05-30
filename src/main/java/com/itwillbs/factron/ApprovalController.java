package com.itwillbs.factron;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApprovalController {

    @GetMapping ("/approval")
    public String main() {
        return "approval/approval.html";
    }

}
