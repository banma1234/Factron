package com.itwillbs.factron.controller.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SalesApprovalController {

    // 영업결재 페이지
    @GetMapping("/salesApproval")
    public String salesApproval() {
        return "approval/salesApproval";
    }

    // 영업결재 상세 폼 (발주, 수주)
    @GetMapping("/salesApproval/{formType}-form")
    public String salesApprovalForm(@PathVariable String formType) {
        // formType: transferApproval, vacationApproval, workApproval
        return "approval/" + formType + "-form";
    }
}
