package com.itwillbs.factron.controller.machine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MachineController {

    @GetMapping("/machine")
    public String machine() {
        return "/machine/machine";
    }

    @GetMapping("/machine-form")
    public String machineFrom() {
        return "/machine/machine-form";
    }

    @GetMapping("/machine-newForm")
    public String machineNewFrom() {
        return "/machine/machine-add-form";
    }
}
