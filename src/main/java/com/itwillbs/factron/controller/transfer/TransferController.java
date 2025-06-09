package com.itwillbs.factron.controller.transfer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransferController {

    /*
     * 인사발령 목록 페이지
     */
    @GetMapping("/trans")
    public String trans() {
        return "transfer/transfer";
    }

    /*
     * 인사발령 폼 페이지
     */
    @GetMapping("/trans/save")
    public String transForm() {
        return "transfer/transfer-form";
    }
}
