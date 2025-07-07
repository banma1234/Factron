package com.itwillbs.factron.service.inbound;

import com.itwillbs.factron.dto.inbound.RequestInboundCompleteDTO;
import com.itwillbs.factron.dto.inbound.RequestInboundDTO;
import com.itwillbs.factron.dto.inbound.RequestSearchInboundDTO;
import com.itwillbs.factron.dto.inbound.ResponseSearchInboundDTO;

import java.util.List;

public interface InboundService {

    // 입고 전체 조회
    List<ResponseSearchInboundDTO> getInboundsList(RequestSearchInboundDTO requestSearchInboundDTO);

    // 입고 처리
    void updateInbound(RequestInboundCompleteDTO dto);
}
