package com.itwillbs.factron.controller.purchase;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PurchaseController {
    @GetMapping("/purchase")
    public String purchase() {
        return "purchase/purchase";
    }
    @GetMapping("/purchaseRegister-form")
    public String purchaseRegistorForm() {
        return "purchase/purchaseRegister-form";
    }
    @GetMapping("/purchaseDetail-form")
    public String purchaseDetailForm() {
        return "purchase/purchaseDetail-form";
    }
}
