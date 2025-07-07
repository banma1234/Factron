package com.itwillbs.factron.service.outbound;

import com.itwillbs.factron.dto.outbound.RequestOutboundCompleteDTO;
import com.itwillbs.factron.dto.outbound.RequestSearchOutboundDTO;
import com.itwillbs.factron.dto.outbound.ResponseSearchOutboundDTO;

import java.util.List;

public interface OutboundService {

    // 출고 전체 조회
    List<ResponseSearchOutboundDTO> getOutboundsList(RequestSearchOutboundDTO requestSearchOutboundDTO);

    // 출고 처리
    void updateOutbound(RequestOutboundCompleteDTO dto);
}
