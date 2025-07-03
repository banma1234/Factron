package com.itwillbs.factron.controller.item;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
public class ItemController {

    /*
     * ITEM 목록 페이지
     */
    @GetMapping("")
    public String itemGrid() {
        return "item/item";
    }
}