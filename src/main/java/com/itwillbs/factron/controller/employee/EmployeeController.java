package com.itwillbs.factron.controller.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class employeeController {

    @GetMapping("")
    public String employee() {
        return "/layout/employee/employee";
    }
}


