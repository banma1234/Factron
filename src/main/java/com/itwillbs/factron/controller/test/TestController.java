package com.itwillbs.factron.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    /*
     * 테스트 목록 페이지
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /*
     * 테스트 폼 페이지
     */
    @GetMapping("/test-form")
    public String testForm() {
        return "test-form";
    }

    @GetMapping("/cookie-test")
    public String cookieTestPage() {
        return "test-cookie";
    }
}
