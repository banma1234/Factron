package com.itwillbs.factron.common.component;

import com.itwillbs.factron.entity.enums.LotType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LotIdGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD

    /**
     * LotType(입고,검사) 기반으로 LOT ID 생성
     */
    public String generate(LocalDate date, LotType type, long sequence) {
        return format(date, type.getPrefix(), sequence);
    }

    /**
     * 공통코드(공정) 기반으로 LOT ID 생성
     */
    public String generate(LocalDate date, String prefix, long sequence) {
        return format(date, prefix, sequence);
    }

    private String format(LocalDate date, String prefix, long sequence) {
        String datePart = date.format(DATE_FORMAT); // e.g. 20250625
        return String.format("%s-%s-%04d", datePart, prefix, sequence);
    }
}

// 20180501-입고-0001 : 목재합판
// 20180501-입고-0002 : 나사
// 20180501-INBOUND-0003 : 볼트
// 20180501-QUALITY-0001 : 검사
// 20180501-제품코드-0001 : 제품?

// 20180501-PCS001-0001
// 20180501-PCS001-0002
// 20180501-PCS001-0003
// 20180501-PCS005-0001

// ========================================

// 20180501-입고-0001 (13:00) : 나사
// 20180501-입고-0002 (13:00) : 볼트
// 20180501-단조-0003 (13:05) : 단조
// 20180501-입고-0004 (13:10) : 목재
// 20180501-검사-0005 (13:12) : 검사