package com.itwillbs.factron.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BOMController {

    /*
     * BOM 목록 페이지
     */
    @GetMapping("/bom")
    public String bom() {
        return "product/bom";
    }
}
