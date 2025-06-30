package com.itwillbs.factron.service.outbound;

import com.itwillbs.factron.dto.outbound.RequestOutboundCompleteDTO;
import com.itwillbs.factron.dto.outbound.RequestSearchOutboundDTO;
import com.itwillbs.factron.dto.outbound.ResponseSearchOutboundDTO;

import java.util.List;

public interface OutboundService {
    List<ResponseSearchOutboundDTO> getOutboundsList(RequestSearchOutboundDTO requestSearchOutboundDTO);
    void updateOutbound(RequestOutboundCompleteDTO dto);
}
