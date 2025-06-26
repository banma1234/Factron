package com.itwillbs.factron.entity.enums;

import lombok.Getter;

@Getter
public enum LotType {
    MATERIAL("INBOUND", "입고"),
    INSPECTION("QUALITY", "검사");

    private final String prefix;
    private final String description;

    LotType(String prefix, String description) {
        this.prefix = prefix;
        this.description = description;
    }
}