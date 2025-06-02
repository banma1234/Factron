package com.itwillbs.factron.controller.syscode;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SysController {

    @GetMapping("/sys")
    public String sys() { return "sys/syscode"; }

    @GetMapping("/sys-form")
    public String sysForm() { return "sys/syscode-form"; }
}
