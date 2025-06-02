package com.itwillbs.factron.controller.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApprovalController {

    // 전자결재 페이지
    @GetMapping ("/approval")
    public String approval() {
        return "approval/approval";
    }

    //결재 서류 폼 페이지
    @GetMapping("/approval/")
    public String approval_form(){return "approval/approval";}
}
