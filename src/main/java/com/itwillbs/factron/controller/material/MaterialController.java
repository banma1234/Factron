package com.itwillbs.factron.controller.material;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/material")
public class MaterialController {

    @GetMapping("")
    public String materialGrid() {
        return "material/material";
    }
}