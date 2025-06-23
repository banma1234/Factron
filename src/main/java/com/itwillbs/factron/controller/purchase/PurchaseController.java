package com.itwillbs.factron.controller.purchase;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PurchaseController {
    @GetMapping("/purchase")
    public String purchase() {
        return "purchase/purchase";
    }
    @GetMapping("/purchase-form")
    public String purchaseForm() {
        return "purchase/purchase-form";
    }
}
