package com.itwillbs.factron.controller.contract;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContractController {
    @GetMapping("/contract")
    public String contract() {
        return "contract/contract";
    }
    @GetMapping("/contract-form")
    public String contractForm() {
        return "contract/contract-form";
    }
}
