package com.itwillbs.factron.controller.approval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApprovalController {

    // 전자결재 페이지
    @GetMapping ("/approval")
    public String approval() {
        return "approval/approval";
    }

    // 공통 결재 폼 (데이터는 postMessage로 전달받음)
    @GetMapping("/approval/{formType}-form")
    public String approvalForm(@PathVariable String formType) {
        // formType: transferApproval, vacationApproval, workApproval
        return "approval/" + formType + "-form";
    }
}
