package com.itwillbs.factron.service.inbound;

import com.itwillbs.factron.dto.inbound.RequestInboundCompleteDTO;
import com.itwillbs.factron.dto.inbound.RequestInboundDTO;
import com.itwillbs.factron.dto.inbound.RequestSearchInboundDTO;
import com.itwillbs.factron.dto.inbound.ResponseSearchInboundDTO;

import java.util.List;

public interface InboundService {
    List<ResponseSearchInboundDTO> getInboundsList(RequestSearchInboundDTO requestSearchInboundDTO);
    void updateInbound(RequestInboundCompleteDTO dto);
}
