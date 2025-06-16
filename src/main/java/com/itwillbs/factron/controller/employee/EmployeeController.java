package com.itwillbs.factron.controller.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    @GetMapping("/employee")
    public String employee() {
        return "/employee/employee";
    }

    @GetMapping("/employee-form")
    public String employeeFrom() {
        return "/employee/employee-form";
    }

    @GetMapping("/employee-newForm")
    public String employeeNewFrom() {
        return "/employee/employee-add-form";
    }
}


