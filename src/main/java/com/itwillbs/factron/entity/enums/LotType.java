package com.itwillbs.factron.entity.enums;

import lombok.Getter;

import java.util.Arrays;

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

    public static String getDescriptionByPrefix(String prefix) {
        return Arrays.stream(LotType.values())
                .filter(type -> type.getPrefix().equals(prefix))
                .findFirst()
                .map(LotType::getDescription)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prefix: " + prefix));
    }
}