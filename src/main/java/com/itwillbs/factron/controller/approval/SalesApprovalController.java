package com.itwillbs.factron.controller.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SalesApprovalController {
    @GetMapping("/salesApproval")
    public String salesApproval() {
        return "approval/salesApproval";
    }
    @GetMapping("/salesApproval/{formType}-form")
    public String salesApprovalForm(@PathVariable String formType) {
        // formType: transferApproval, vacationApproval, workApproval
        return "approval/" + formType + "-form";
    }
}
