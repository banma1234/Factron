package com.itwillbs.factron.controller.stock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockController {
    @GetMapping("/stock")
    public String stock() { return "stock/stock"; }
}
