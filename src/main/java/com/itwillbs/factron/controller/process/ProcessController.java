package com.itwillbs.factron.controller.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    @GetMapping("/process")
    public String process() {
        return "/process/process";
    }

    @GetMapping("/process-form")
    public String processFrom() {
        return "/process/process-form";
    }

    @GetMapping("/process-newForm")
    public String processNewFrom() {
        return "/process/process-add-form";
    }
}
