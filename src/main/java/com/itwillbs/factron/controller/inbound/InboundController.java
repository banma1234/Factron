package com.itwillbs.factron.controller.inbound;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InboundController {
    @GetMapping("/inbound")
    public String inbound() {
        return "inbound/inbound";
    }
}
