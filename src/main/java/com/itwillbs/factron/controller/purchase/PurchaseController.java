package com.itwillbs.factron.controller.purchase;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PurchaseController {

    // 발주 페이지
    @GetMapping("/purchase")
    public String purchase()
    {
        return "purchase/purchase";
    }

    // 발주 등록 폼
    @GetMapping("/purchaseRegister-form")
    public String purchaseRegistorForm()
    {
        return "purchase/purchaseRegister-form";
    }

    // 발주 상세 폼
    @GetMapping("/purchaseDetail-form")
    public String purchaseDetailForm()
    {
        return "purchase/purchaseDetail-form";
    }
}
