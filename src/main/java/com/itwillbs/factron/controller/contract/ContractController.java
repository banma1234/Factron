package com.itwillbs.factron.controller.contract;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContractController {

    // 수주 페이지
    @GetMapping("/contract")
    public String contract() {
        return "contract/contract";
    }

    // 수주 등록 폼
    @GetMapping("/contractRegister-form")
    public String contractRegisterForm() {
        return "contract/contractRegister-form";
    }

    //수주 상세 폼
    @GetMapping("/contractDetail-form")
    public String contractDetailForm() {
        return "contract/contractDetail-form";
    }
}
