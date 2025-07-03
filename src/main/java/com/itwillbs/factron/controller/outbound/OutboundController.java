package com.itwillbs.factron.controller.outbound;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutboundController {
    @GetMapping("/outbound")
    public String outbound() {
        return "outbound/outbound";
    }
}
