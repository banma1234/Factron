package com.itwillbs.factron.entity.enums;

import lombok.Getter;

@Getter
public enum LotType {
    INBOUND("INB", "입고"),
    QUALITY("ISP", "검사");

    private final String prefix;
    private final String description;

    LotType(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }
}