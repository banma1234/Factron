package com.itwillbs.factron.dto.inbound;

import lombok.Data;

@Data
public class RequestSearchInboundDTO {
    private String srhItemOrMaterialName;// 원자재id or 원자재명
    private String startDate;            // 시작일 (YYYY-MM-DD)
    private String endDate;              // 종료일 (YYYY-MM-DD)
}
