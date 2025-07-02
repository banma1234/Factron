package com.itwillbs.factron.controller.lot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lot")
public class LotController {

    @GetMapping("")
    public String lot() { return "lot/lot"; }
}
