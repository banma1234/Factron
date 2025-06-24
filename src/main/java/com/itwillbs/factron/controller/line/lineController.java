package com.itwillbs.factron.controller.line;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class lineController {

    @GetMapping("/line")
    public String line() {
        return "/line/line";
    }

    @GetMapping("/line-form")
    public String lineFrom() {
        return "/line/line-form";
    }

    @GetMapping("/line-newForm")
    public String lineNewFrom() {
        return "/line/line-add-form";
    }
}
