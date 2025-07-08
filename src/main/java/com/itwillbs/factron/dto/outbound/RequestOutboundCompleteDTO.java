package com.itwillbs.factron.dto.outbound;

import lombok.Data;

import java.util.List;

@Data
public class RequestOutboundCompleteDTO {
    private List<Long> outboundIds; // 체크된 출고 ID들
}
