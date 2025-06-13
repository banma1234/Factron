package com.itwillbs.factron.controller.sys;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sys")
public class SysController {

    @GetMapping("")
    public String sys() { return "sys/syscode"; }

    @GetMapping("/sys-form")
    public String sysForm() { return "sys/syscode-form"; }
}