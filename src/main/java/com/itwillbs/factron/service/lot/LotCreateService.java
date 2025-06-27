package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;

import java.util.List;

public interface LotCreateService {

    public Void CreateInboundLot(List<RequestInboundLotDTO> reqInbound);

    public Void CreateProcessLot(RequestProcessLotDTO reqInbound);
}
