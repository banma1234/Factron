package com.itwillbs.factron.controller.production;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WorkOrderController {

    /*
     * 작업지시 목록 페이지
     */
    @GetMapping("/workorder")
    public String workOrder() {
        return "production/workOrder";
    }

    /*
     * 작업지시 등록 폼 페이지
     */
    @GetMapping("/workorder/save")
    public String workOrderSaveForm() {
        return "production/workOrder-add-form";
    }

    /*
     * 작업지시 상세 폼 페이지
     */
    @GetMapping("/workorder-form")
    public String workOrderForm() {
        return "production/workOrder-form";
    }
}
