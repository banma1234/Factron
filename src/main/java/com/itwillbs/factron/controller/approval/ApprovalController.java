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

    // 공통 결재 폼 핸들러
//    @GetMapping("/approval/{formType}-form")
//    public String approvalForm(
//            @RequestParam(name = "id") String approvalId,
//            @RequestParam(name = "userId", required = false) String userId,
//            @RequestParam(name = "authCode", required = false) String authCode,
//            @org.springframework.web.bind.annotation.PathVariable String formType,
//            Model model
//    ) {
//        model.addAttribute("approvalId", approvalId);
//        model.addAttribute("userId", userId);
//        model.addAttribute("authCode", authCode);
//
//        // formType은 "transferApproval", "vacationApproval", "workApproval" 중 하나
//        return "approval/" + formType + "-form";
//    }
}
