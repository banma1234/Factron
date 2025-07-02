package com.itwillbs.factron.dto.outbound;

import lombok.Data;

@Data
public class RequestSearchOutboundDTO {
    private String srhItemOrItemName;
    private String startDate;            // 시작일 (YYYY-MM-DD)
    private String endDate;              // 종료일 (YYYY-MM-DD)
}
