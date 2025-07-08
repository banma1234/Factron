package com.itwillbs.factron.dto.inbound;

import lombok.Data;

import java.util.List;

@Data
public class RequestInboundCompleteDTO {
    private List<Long> inboundIds; // 체크된 입고 ID들
}
