package com.itwillbs.factron.controller.contract;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContractController {
    @GetMapping("/contract")
    public String contract() {
        return "contract/contract";
    }
    @GetMapping("/contractRegister-form")
    public String contractRegisterForm() {
        return "contract/contractRegister-form";
    }
    @GetMapping("/contractDetail-form")
    public String contractDetailForm() {
        return "contract/contractDetail-form";
    }
}
