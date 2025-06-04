package com.itwillbs.factron.controller.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmployeeController {

    @GetMapping("/employee")
    public String employee() {
        return "/layout/employee/employee";
    }

    @GetMapping("/employee-form")
    public String employeeFrom() {
        return "/layout/employee/employee-form";
    }
}


